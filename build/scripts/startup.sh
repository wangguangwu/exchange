#!/bin/bash

# 切换到脚本所在目录的上级目录（项目根目录）
# shellcheck disable=SC2164
cd "$(dirname "$0")/.."

# 启动 docker-compose
echo "Starting Docker Compose services..."
docker-compose up -d

# 检查启动状态
# shellcheck disable=SC2181
if [ $? -eq 0 ]; then
    echo "Docker Compose services started successfully."
else
    echo "Failed to start Docker Compose services."
    exit 1
fi