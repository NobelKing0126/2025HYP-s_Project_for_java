#!/bin/bash

echo "==========================================="
echo "  下载项目依赖库"
echo "==========================================="

LIB_DIR="lib"
mkdir -p $LIB_DIR
cd $LIB_DIR

# 下载函数，如果文件存在则跳过
download_if_not_exists() {
    local url=$1
    local filename=$(basename $url)
    
    if [ -f "$filename" ]; then
        echo "[跳过] $filename 已存在"
    else
        echo "[下载] $filename ..."
        wget -q --show-progress "$url"
    fi
}

download_if_not_exists "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar"
download_if_not_exists "https://repo1.maven.org/maven2/org/apache/poi/poi/5.2.3/poi-5.2.3.jar"
download_if_not_exists "https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.2.3/poi-ooxml-5.2.3.jar"
download_if_not_exists "https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml-lite/5.2.3/poi-ooxml-lite-5.2.3.jar"
download_if_not_exists "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar"
download_if_not_exists "https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar"
download_if_not_exists "https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar"
download_if_not_exists "https://repo1.maven.org/maven2/org/apache/xmlbeans/xmlbeans/5.1.1/xmlbeans-5.1.1.jar"
download_if_not_exists "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar"
download_if_not_exists "https://repo1.maven.org/maven2/commons-codec/commons-codec/1.15/commons-codec-1.15.jar"

cd ..

echo ""
echo "==========================================="
echo "  完成！"
echo "==========================================="
ls -lh $LIB_DIR/*.jar
