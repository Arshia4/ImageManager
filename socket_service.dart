import 'dart:async';
import 'dart:convert';
import 'dart:io';

class SocketService {
  static const String host = '10.214.181.61';
  static const int port = 12345;

  Socket? _socket;
  bool _isConnected = false;
  String _buffer = '';

  bool get isConnected => _isConnected;

  Future<void> connect() async {
    try {
      _socket = await Socket.connect(host, port);
      _isConnected = true;
      print(' Connected to server');

      _socket!.listen(
        (data) {
          _buffer += utf8.decode(data);
          if (_buffer.contains('\n')) {
            final lines = _buffer.split('\n');
            final response = lines.first;
            _buffer = lines.skip(1).join('\n');
            if (_responseCompleter != null &&
                !_responseCompleter!.isCompleted) {
              _responseCompleter!.complete(response);
            }
          }
        },
        onError: (error) {
          if (_responseCompleter != null && !_responseCompleter!.isCompleted) {
            _responseCompleter!.completeError(error);
          }
        },
        onDone: () {
          _isConnected = false;
          if (_responseCompleter != null && !_responseCompleter!.isCompleted) {
            _responseCompleter!.completeError('Connection closed');
          }
        },
      );
    } catch (e) {
      _isConnected = false;
      print(' Connection failed: $e');
      rethrow;
    }
  }

  void disconnect() {
    _socket?.close();
    _isConnected = false;
    _buffer = '';
    _responseCompleter = null;
    print('🔌 Disconnected from server');
  }

  Completer<String>? _responseCompleter;

  Future<String> _receiveResponse() {
    _responseCompleter = Completer<String>();
    return _responseCompleter!.future.timeout(
      const Duration(seconds: 10),
      onTimeout: () {
        throw Exception('Timeout waiting for response');
      },
    );
  }

  Future<Map<String, dynamic>> sendRequest(Map<String, dynamic> request) async {
    if (!_isConnected) {
      await connect();
    }

    try {
      String jsonRequest = jsonEncode(request);
      _socket!.write('$jsonRequest\n');
      await _socket!.flush();

      String response = await _receiveResponse();
      return jsonDecode(response);
    } catch (e) {
      print(' Error sending request: $e');
      rethrow;
    }
  }
}
