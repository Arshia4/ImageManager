import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/app_state.dart';
import 'screens/AuthScreen.dart';
import 'screens/home_screen.dart';
import 'screens/AlbumScreen.dart';
import 'screens/ProfileScreen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) {
        final appState = AppState();
        appState.connect();
        return appState;
      },
      child: MaterialApp(
        title: 'مدیریت تصاویر',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(primarySwatch: Colors.blue, useMaterial3: true),
        initialRoute: '/auth', 
        routes: {
          '/auth': (context) => const AuthScreen(),
          '/home': (context) => const HomeScreen(),
          '/albums': (context) => const AlbumsScreen(),
          '/profile': (context) => const ProfileScreen(),
        },
      ),
    );
  }
}
