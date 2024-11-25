#!/bin/bash

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 先执行 shutdown.sh
SHUTDOWN_SCRIPT="${SCRIPT_DIR}/shutdown.sh"
if [ -f "$SHUTDOWN_SCRIPT" ]; then
    echo "Executing shutdown script..."
    bash "$SHUTDOWN_SCRIPT"
    # shellcheck disable=SC2181
    if [ $? -eq 0 ]; then
        echo "Shutdown script executed successfully."
    else
        echo "Failed to execute shutdown script."
        exit 1
    fi
else
    echo "Shutdown script not found. Skipping."
fi

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