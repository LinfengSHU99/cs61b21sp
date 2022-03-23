# Gitlet Design Document

**Name**:

## Classes and Data Structures

### Repository
&emsp;&emsp;静态工具类
#### Fields

1. CWD 当前工作目录
2. GITLET——DIR 存放gitlet信息的目录
3. blobs 存放所有提交过的所有版本的文件的sha1值（包括历史文件）


### Stage

#### Fields

1. map 存放当前在stage里面的文件名字及其sha1值
2. Field 2

#### Methods

1. add(String name, String sha) 将指定的文件加入到stage，如果stage里已经有一样的版本或者当前commit里有一样的版本则忽略

### Commit

#### Fields

1. message, date, log_sha1, branch 一些元信息
2. map 存放当前已经commit的文件名字及其对应的sha1
3. head 指向当前commit
4. parents 存放当前commit的父节点

#### Methods

1. Commit(Commit ... parent_arr) 初始化一个commit节点，传入的参数是父节点数组，将父节点的map信息都拷贝至初始化的节点。

2. update(String message) 将当前所在的commit更新相关元信息，并把当前stage里的信息加入至当前commit，清除当前stage

## Algorithms

## Persistence

