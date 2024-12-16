INSERT INTO file_signatures (extension, mime_type, hex_signature, description, file_type) VALUES
('jpg', 'image/jpeg', 'FFD8FF', 'JPEG изображение', 'IMAGE'),
('jpeg', 'image/jpeg', 'FFD8FF', 'JPEG изображение', 'IMAGE'),
('png', 'image/png', '89504E47', 'PNG изображение', 'IMAGE'),
('gif', 'image/gif', '474946', 'GIF изображение', 'IMAGE'),
('bmp', 'image/bmp', '424D', 'Bitmap изображение', 'IMAGE'),
('webp', 'image/webp', '52494646', 'WebP изображение', 'IMAGE'),
('tiff', 'image/tiff', '49492A00', 'TIFF изображение', 'IMAGE'),

('mp4', 'video/mp4', '66747970', 'MP4 видео', 'VIDEO'),
('avi', 'video/x-msvideo', '52494646', 'AVI видео', 'VIDEO'),
('mov', 'video/quicktime', '6D6F6F76', 'QuickTime видео', 'VIDEO'),
('mkv', 'video/x-matroska', '1A45DFA3', 'Matroska видео', 'VIDEO'),
('wmv', 'video/x-ms-wmv', '3026B275', 'Windows Media Video', 'VIDEO'),

('mp3', 'audio/mpeg', '494433', 'MP3 аудио', 'AUDIO'),
('wav', 'audio/wav', '52494646', 'WAV аудио', 'AUDIO'),
('flac', 'audio/flac', '664C6143', 'FLAC аудио', 'AUDIO'),
('ogg', 'audio/ogg', '4F676753', 'OGG аудио', 'AUDIO'),

('pdf', 'application/pdf', '25504446', 'PDF документ', 'DOCUMENT'),
('docx', 'application/vnd.openxmlformats-office document.wordprocessingml.document', '504B0304', 'Microsoft Word документ', 'DOCUMENT'),
('xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '504B0304', 'Microsoft Excel таблица', 'DOCUMENT'),
('txt', 'text/plain', '', 'Текстовый файл', 'DOCUMENT'),

('zip', 'application/zip', '504B0304', 'ZIP архив', 'ARCHIVE'),
('rar', 'application/x-rar-compressed', '526172211A0700', '7-ZIP архив', 'ARCHIVE'),
('7z', 'application/x-7z-compressed', '377ABCAF271C', '7-ZIP архив', 'ARCHIVE'),
('tar', 'application/x-tar', '7461722000', 'TAR архив', 'ARCHIVE'),
('gz', 'application/gzip', '1F8B08', 'GZIP архив', 'ARCHIVE'),

('exe', 'application/x-msdownload', '4D5A', 'Windows исполняемый файл', 'EXECUTABLE'),
('dll', 'application/x-msdownload', '4D5A', 'Windows библиотека', 'EXECUTABLE');