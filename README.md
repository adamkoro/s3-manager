# S3 Manager

S3 Manager is a web application for managing files in Amazon S3 or other S3 compatible buckets. It provides a user interface to upload, download, and delete files. You can add S3 endpoints via web ui

## Features
- Upload files to S3 buckets
- Download files from S3 buckets
- Delete files from S3 buckets
- User-friendly web interface

## Installation

### Clone the repository

```bash
git clone https://github.com/adamkoro/s3-manager.git

cd s3-manager
```

### Start prod compose

If u wanted to check the application

```bash
docker compose --project-directory compose/prod/ up -d
```

After keycloak is started, you have to create user in s3-manager realm. After that you can access the application


