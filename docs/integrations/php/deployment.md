# Gush Security — PHP Deployment Guide

## 1. Deploying to Shared Web Hosting (cPanel)

1. Compress `/examples/php-server/` into a `.zip` archive.
2. Log into your **cPanel Account** and open **File Manager**.
3. Upload the archive into `public_html/gush-security/` and extract it.
4. Set folder permissions to `755` and file permissions to `644`.
5. Create `.env` from `.env.example` in the directory root (above `public/` if supported, or protected by `.htaccess`).
6. In cPanel **MySQL Databases**, create your database and run `database/schema.sql`.
7. Verify that `https://yourdomain.com/gush-security/public/health.php` responds with `status: ok`.

---

## 2. Deploying to a Linux VPS (Ubuntu 22.04 / 24.04)

```bash
# 1. Install PHP 8.1+ & Extensions
sudo apt update
sudo apt install -y php8.1-fpm php8.1-mysql php8.1-curl php8.1-mbstring php8.1-xml nginx mysql-server

# 2. Clone / Copy Repository
cd /var/www/
sudo git clone https://github.com/gushed29/Gush-Estate-Security-App.git
cd Gush-Estate-Security-App/examples/php-server

# 3. Set Permissions
sudo chown -R www-data:www-data storage/
sudo chmod -R 775 storage/

# 4. Configure .env
cp .env.example .env
nano .env

# 5. Link Nginx Virtual Host and Restart
sudo ln -s /var/www/Gush-Estate-Security-App/docs/integrations/php/nginx.conf /etc/nginx/sites-enabled/gush.conf
sudo systemctl reload nginx
sudo systemctl restart php8.1-fpm
```
