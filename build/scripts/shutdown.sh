#!/bin/bash

# 切换到脚本所在目录的上级目录（项目根目录）
# shellcheck disable=SC2164
cd "$(dirname "$0")/.."

# 关闭 docker-compose
echo "Stopping Docker Compose services..."
docker-compose down

# 检查停止状态
# shellcheck disable=SC2181
if [ $? -eq 0 ]; then
    echo "Docker Compose services stopped successfully."
else
    echo "Failed to stop Docker Compose services."
    exit 1
fi