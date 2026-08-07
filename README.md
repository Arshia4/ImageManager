# پروژه اشتراک‌گذاری عکس (Photo Sharing App)

یک اپلیکیشن اشتراک‌گذاری عکس با معماری کلاینت-سرور، شامل:
- **بک‌اند (Java):** سرور TCP Socket که با پیام‌های JSON کار می‌کند.
- **فرانت‌اند (Flutter/Dart):** اپلیکیشن موبایل که از طریق سوکت با سرور ارتباط برقرار می‌کند.

## ساختار پروژه

```
backend/
  src/main/java/ap/photo/
    Server.java            نقطه شروع سرور، پذیرش اتصالات
    ClientHandler.java     مسیریابی درخواست‌ها و پاسخ‌دهی به هر کلاینت
    RequestParser.java     پارس کردن JSON ورودی
    ResponseBuilder.java   ساخت پاسخ JSON
    Database.java          نگهداری داده در حافظه + ذخیره/بارگذاری JSON
    DatabaseManager.java   منطق اصلی (کاربر، عکس، آلبوم، کامنت، لایک، تگ)
    FileManager.java       ذخیره/حذف فایل عکس روی دیسک
    User.java / Image.java / Album.java / Comment.java / Tag.java  مدل‌ها
    AdminPanel.java        عملیات مدیریتی (لیست کاربران، بن/آنبن و ...)
    PasswordIncorrectException.java

frontend/
  Main.dart
  models/        Album.dart, User.dart, comment.dart, image_item.dart
  providers/      app_state.dart (وضعیت اپ + ارتباط با سوکت)
  services/       socket_service.dart (اتصال TCP و ارسال/دریافت JSON)
  screens/        صفحات UI (ورود، خانه، آلبوم، جزئیات عکس، آپلود، جستجو، پروفایل)
  widgets/        AlbumCard.dart
```

## نحوه اجرا

### سرور (بک‌اند)
1. وارد پوشه `backend` شوید.
2. پروژه را با Maven/Gradle یا مستقیم با `javac` کامپایل کنید (وابستگی: Gson).
3. `Server.java` را اجرا کنید. سرور روی پورت `12345` بالا می‌آید.

> ⚠️ مسیر ذخیره‌سازی عکس‌ها در `FileManager.java` به‌صورت هارد-کد روی `C:/uploads/` تنظیم شده و فقط روی ویندوز کار می‌کند.

### کلاینت (فرانت‌اند)
1. وارد پوشه `frontend` شوید (باید داخل یک پروژه‌ی استاندارد Flutter قرار بگیرد؛ فایل `pubspec.yaml` در آرشیو موجود نبود).
2. آدرس و پورت سرور را در `services/socket_service.dart` تنظیم کنید.
3. با `flutter run` اجرا کنید.

## پروتکل ارتباطی

ارتباط از طریق TCP Socket با پیام‌های خط‌به‌خط JSON انجام می‌شود:

```json
{ "type": "LOGIN", "data": { "username": "...", "password": "..." } }
```

## باگ‌های شناسایی‌شده (نیاز به رفع)

هنگام بررسی کد، دقیقاً همان مشکلاتی که گزارش شده بود در کد پیدا شد:

### ۱. فایل `database.json` هیچ‌وقت ساخته نمی‌شود
در `Server.java` فقط `database.loadFromFile("database.json")` صدا زده می‌شود، اما `database.saveToFile(...)` در **هیچ‌جای پروژه** فراخوانی نشده. یعنی داده‌ها فقط در RAM می‌مانند و با ری‌استارت سرور از بین می‌روند.

**راه‌حل پیشنهادی:** بعد از هر عملیات نوشتنی (ثبت‌نام، آپلود عکس، حذف، ساخت آلبوم و...) در `DatabaseManager` یا `ClientHandler`، متد `database.saveToFile("database.json")` را صدا بزنید. ساده‌ترین راه، فراخوانی آن در انتهای `handleRequest(...)` در `ClientHandler.java` است (فقط برای type هایی که داده را تغییر می‌دهند).

### ۲. حذف آلبوم اصلاً پیاده‌سازی نشده
- فرانت‌اند در `app_state.dart` پیام با `'type': 'DELETE_ALBUM'` می‌فرستد.
- اما در `ClientHandler.java` هیچ `case "DELETE_ALBUM"` وجود ندارد (فقط `default` که خطای «نوع درخواست نامعتبر» برمی‌گرداند).
- در `DatabaseManager.java` هم اصلاً متدی به نام `deleteAlbum` وجود ندارد.

**راه‌حل:** یک متد `deleteAlbum(albumId, userId)` در `DatabaseManager` بنویسید (که آلبوم و رکورد `albumImages` مربوطه را حذف کند)، سپس در `ClientHandler` یک `case "DELETE_ALBUM"` و متد `handleDeleteAlbum` اضافه کنید.

### ۳. حذف چند عکس با هم و حذف کامنت هم پیاده‌سازی نشده
فرانت‌اند این تایپ‌ها را می‌فرستد ولی بک‌اند برایشان `case` ندارد:
- `DELETE_IMAGES` (نسخه‌ی جمع؛ متد معادلش `deleteImages` در `DatabaseManager` وجود دارد ولی به سوکت وصل نشده)
- `DELETE_COMMENT`
- `EDIT_IMAGE_TITLE`, `EDIT_IMAGE_CAPTION`, `EDIT_IMAGE_TAGS`
- `REMOVE_FROM_ALBUM` (متد `removeImageFromAlbum` در `DatabaseManager` هست ولی روت نشده)

**راه‌حل:** برای هرکدام یک `case` و `handle...` متد متناظر در `ClientHandler.java` اضافه کنید که متد آماده‌ی موجود در `DatabaseManager` را صدا بزند.

### ۴. اضافه شدن عکس به آلبوم
مسیر `ADD_TO_ALBUM` در بک‌اند وجود دارد و کار می‌کند، اما هیچ اعتبارسنجی ندارد (اگر `imageId` یا `albumId` نامعتبر باشد باز هم پیام موفقیت برمی‌گرداند). اگر در فرانت‌اند مشکلی دیده می‌شود، احتمالاً به‌خاطر عدم ذخیره شدن `database.json` (مورد ۱) است که با ری‌استارت سرور، عکس‌ها و آلبوم‌ها گم می‌شوند و id ها دیگر معتبر نیستند — یا نام کلیدهای ارسالی از فرانت (`imageId`/`albumId`) با چیزی که سرور انتظار دارد یکی نیست؛ بهتر است لاگ کنسول سرور (`📩 دریافت درخواست`) هنگام تست چک شود.

## خلاصه‌ی کارهای لازم برای رفع مشکلات گزارش‌شده

| مشکل | علت | فایل مربوطه |
|---|---|---|
| JSON دیتابیس ساخته نمی‌شود | `saveToFile` هیچ‌جا صدا زده نمی‌شود | `Server.java` / `ClientHandler.java` |
| عکس حذف نمی‌شود | اگر `imageId`/`userId` نادرست ارسال شود یا داده قبل از ری‌استارت ذخیره نشده | `DatabaseManager.deleteImage` |
| آلبوم حذف نمی‌شود | اصلاً پیاده‌سازی نشده (نه در Manager نه در Handler) | `DatabaseManager.java`, `ClientHandler.java` |
| عکس به آلبوم اضافه نمی‌شود | به‌احتمال زیاد به‌خاطر گم‌شدن داده بعد از ری‌استارت (مورد ۱) | `Server.java` |
