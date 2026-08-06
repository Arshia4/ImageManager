import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/app_state.dart';

class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key});

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLogin = true;
  bool _isLoading = false;

  final RegExp _usernameRegex = RegExp(r'^[a-zA-Z0-9_]{4,16}$');
  final RegExp _passwordRegex = RegExp(
    r'^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$',
  );

  Future<void> _submitForm() async {
    if (_formKey.currentState!.validate()) {
      setState(() => _isLoading = true);

      final appState = Provider.of<AppState>(context, listen: false);
      bool success;

      if (_isLogin) {
        success = await appState.login(
          _usernameController.text,
          _passwordController.text,
        );
      } else {
        success = await appState.register(
          _usernameController.text,
          _passwordController.text,
        );
      }

      setState(() => _isLoading = false);

      if (success) {
        if (mounted) {
          Navigator.pushReplacementNamed(context, '/home');
        }
      } else {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(
                _isLogin
                    ? 'نام کاربری یا رمز اشتباه است'
                    : 'این نام کاربری قبلا گرفته شده',
              ),
            ),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                _isLogin ? 'ورود به حساب' : 'ثبت نام جدید',
                style: const TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 32),
              TextFormField(
                controller: _usernameController,
                decoration: const InputDecoration(labelText: 'نام کاربری'),
                validator: (value) {
                  if (value == null || !_usernameRegex.hasMatch(value)) {
                    return '۴ تا ۱۶ کاراکتر انگلیسی یا عدد و زیرخط';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _passwordController,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'رمز عبور'),
                validator: (value) {
                  if (value == null || !_passwordRegex.hasMatch(value)) {
                    return 'حداقل ۸ کاراکتر شامل حداقل یک حرف و یک عدد';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _submitForm,
                  child: _isLoading
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: Colors.white,
                          ),
                        )
                      : Text(_isLogin ? 'ورود' : 'ثبت نام'),
                ),
              ),
              TextButton(
                onPressed: _isLoading
                    ? null
                    : () => setState(() => _isLogin = !_isLogin),
                child: Text(
                  _isLogin
                      ? 'ایجاد حساب کاربری جدید'
                      : 'قبلاً ثبت نام کرده‌ام؟ ورود',
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
