import 'package:flutter/material.dart';
import '../models/user.dart';
import '../models/album.dart';
import '../models/image_item.dart';
import '../models/comment.dart';
import '../services/socket_service.dart';

class AppState extends ChangeNotifier {
  List<ImageItem> images = [];
  List<Album> albums = [];
  List<Comment> comments = [];
  User? currentUser;

  final SocketService _socketService = SocketService();

  bool get isConnected => _socketService.isConnected;

  Future<void> connect() async {
    await _socketService.connect();
    notifyListeners();
  }

  void disconnect() {
    _socketService.disconnect();
    currentUser = null;
    images = [];
    albums = [];
    comments = [];
    notifyListeners();
  }

  Future<bool> login(String username, String password) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'LOGIN',
        'data': {'username': username, 'password': password},
      });

      if (response['status'] == 'OK') {
        final data = response['data'];
        currentUser = User(
          userId: data['userId'],
          username: data['username'],
          password: '',
          imageCount: data['imageCount'] ?? 0,
          albumCount: data['albumCount'] ?? 0,
        );
        await loadUserData();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> register(String username, String password) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'REGISTER',
        'data': {'username': username, 'password': password},
      });

      if (response['status'] == 'OK') {
        final data = response['data'];
        currentUser = User(
          userId: data['userId'],
          username: data['username'],
          password: '',
          imageCount: 0,
          albumCount: 0,
        );
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  void logout() {
    disconnect();
  }

  Future<void> loadUserData() async {
    if (currentUser == null) return;
    await Future.wait([loadImages(), loadAlbums()]);
  }

  Future<void> loadImages() async {
    if (currentUser == null) return;
    try {
      final response = await _socketService.sendRequest({
        'type': 'GET_IMAGES',
        'data': {'userId': currentUser!.userId},
      });

      if (response['status'] == 'OK') {
        final List<dynamic> data = response['data'] ?? [];
        images = data
            .map(
              (item) => ImageItem(
                imageId: item['imageId'] ?? '',
                userId: item['userId'] ?? currentUser!.userId,
                title: item['title'] ?? '',
                caption: item['caption'] ?? '',
                uploadDate: item['uploadDate'] ?? DateTime.now().toString(),
                isLiked: item['isLiked'] ?? false,
                tags: List<String>.from(item['tags'] ?? []),
                albumIds: List<String>.from(item['albumIds'] ?? []),
                imageData: item['imageData'] ?? '', 
                
              ),
            )
            .toList();
        notifyListeners();
      }
    } catch (e) {
      print('Error loading images: $e');
    }
  }

  Future<void> loadAlbums() async {
    if (currentUser == null) return;
    try {
      final response = await _socketService.sendRequest({
        'type': 'GET_ALBUMS',
        'data': {'userId': currentUser!.userId},
      });

      if (response['status'] == 'OK') {
        final List<dynamic> data = response['data'] ?? [];
        albums = data
            .map(
              (item) => Album(
                albumId: item['albumId'] ?? '',
                name: item['name'] ?? '',
                imageIds: List<String>.from(item['imageIds'] ?? []),
              ),
            )
            .toList();
        notifyListeners();
      }
    } catch (e) {
      print('Error loading albums: $e');
    }
  }

  Future<bool> addImageWithBase64(ImageItem image, String base64Image) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'ADD_IMAGE',
        'data': {
          'userId': currentUser?.userId ?? '',
          'title': image.title,
          'caption': image.caption,
          'tags': image.tags,
          'imageData': base64Image,
        },
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      print('Error uploading image: $e');
      return false;
    }
  }

  Future<bool> deleteImage(String imageId) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'DELETE_IMAGE',
        'data': {'imageId': imageId, 'userId': currentUser?.userId ?? ''},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deleteImages(List<String> imageIds) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'DELETE_IMAGES',
        'data': {'imageIds': imageIds, 'userId': currentUser?.userId ?? ''},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> toggleLike(String imageId) async {
    try {
      final image = images.firstWhere((img) => img.imageId == imageId);
      final type = image.isLiked ? 'REMOVE_LIKE' : 'ADD_LIKE';

      final response = await _socketService.sendRequest({
        'type': type,
        'data': {'imageId': imageId, 'userId': currentUser?.userId ?? ''},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> editImageTitle(String imageId, String newTitle) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'EDIT_IMAGE_TITLE',
        'data': {'imageId': imageId, 'newTitle': newTitle},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> editImageCaption(String imageId, String newCaption) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'EDIT_IMAGE_CAPTION',
        'data': {'imageId': imageId, 'newCaption': newCaption},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> editImageTags(String imageId, List<String> newTags) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'EDIT_IMAGE_TAGS',
        'data': {'imageId': imageId, 'newTags': newTags},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> addComment(String imageId, String text) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'ADD_COMMENT',
        'data': {
          'imageId': imageId,
          'userId': currentUser?.userId ?? '',
          'text': text,
        },
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> editComment(String commentId, String newText) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'EDIT_COMMENT',
        'data': {'commentId': commentId, 'newText': newText},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deleteComment(String commentId) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'DELETE_COMMENT',
        'data': {'commentId': commentId},
      });

      if (response['status'] == 'OK') {
        await loadImages();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<List<Comment>> getCommentsForImage(String imageId) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'GET_COMMENTS',
        'data': {'imageId': imageId},
      });

      if (response['status'] == 'OK') {
        final List<dynamic> data = response['data'] ?? [];
        return data
            .map(
              (item) => Comment(
                commentId: item['commentId'] ?? '',
                userId: item['userId'] ?? '',
                imageId: imageId,
                commentText: item['text'] ?? '',
                sendTime: item['sendTime'] ?? DateTime.now().toString(),
              ),
            )
            .toList();
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  Future<bool> createAlbum(String name) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'CREATE_ALBUM',
        'data': {'userId': currentUser?.userId ?? '', 'name': name},
      });

      if (response['status'] == 'OK') {
        await loadAlbums();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> deleteAlbum(String albumId) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'DELETE_ALBUM',
        'data': {'albumId': albumId, 'userId': currentUser?.userId ?? ''},
      });

      if (response['status'] == 'OK') {
        await loadAlbums();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> addImageToAlbum(String imageId, String albumId) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'ADD_TO_ALBUM',
        'data': {'imageId': imageId, 'albumId': albumId},
      });

      if (response['status'] == 'OK') {
        await loadAlbums();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<bool> removeImageFromAlbum(String imageId, String albumId) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'REMOVE_FROM_ALBUM',
        'data': {'imageId': imageId, 'albumId': albumId},
      });

      if (response['status'] == 'OK') {
        await loadAlbums();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<List<ImageItem>> searchImages(String query) async {
    try {
      final response = await _socketService.sendRequest({
        'type': 'SEARCH_IMAGES',
        'data': {'query': query},
      });

      if (response['status'] == 'OK') {
        final List<dynamic> data = response['data'] ?? [];
        return data
            .map(
              (item) => ImageItem(
                imageId: item['imageId'] ?? '',
                userId: item['userId'] ?? '',
                title: item['title'] ?? '',
                caption: item['caption'] ?? '',
                uploadDate: item['uploadDate'] ?? DateTime.now().toString(),
                isLiked: false,
                tags: List<String>.from(item['tags'] ?? []),
                albumIds: List<String>.from(item['albumIds'] ?? []),
                imageData: item['imageData'] ?? '',
              ),
            )
            .toList();
      }
      return [];
    } catch (e) {
      print('Search error: $e');
      return [];
    }
  }
}
