class Album {
  String albumId;
  String name;
  List<String> imageIds;

  Album({required this.albumId, required this.name, List<String>? imageIds})
    : imageIds = imageIds ?? [];
}
