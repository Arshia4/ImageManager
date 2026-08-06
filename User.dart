class User {
  String userId;
  String username;
  String password;
  int imageCount;
  int albumCount;

  User({
    required this.userId,
    required this.username,
    required this.password,
    this.imageCount = 0,
    this.albumCount = 0,
  });
}
