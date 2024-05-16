#!/bin/bash

# Docker 설치를 위한 리포지토리 설정 및 Docker CE 설치
sudo apt update
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
sudo apt update
sudo apt install docker-ce

# OpenJDK 17 JDK 설치
sudo apt update
sudo apt install openjdk-17-jdk
