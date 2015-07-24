# atcfs
Simple file storage server and client library in Java.

Features:
- CRUD operations on files via REST API
- Assign a group of files to some JSON records
- CRUD opreations on that JSON records
- Simple authorization via custom HTTP headers for all operations
- Expirable temporary links to download the files without authorization
- Full support for non-latin characters in file names

Implementation:
- Spring Boot for REST API
- PostgreSQL for storing JSON records and files metadata
- Guava cache for temporary links
- Storing files on disk in folders representing file creation time: <atcfs basedir>/YYYY-MM-DD/HH/
- Split one-hour folder to subfolders when the configured "filesperdirectory" limit exceeded
