FROM mysql:8.0
#
#ENV MYSQL_ROOT_PASSWORD=123456
#ENV MYSQL_DATABASE=screen_vault
#
#COPY init.sql /docker-entrypoint-initdb.d/

# set character set and collation
CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]