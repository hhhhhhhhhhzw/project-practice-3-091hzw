# Android Studio 的安装与使用

官方下载链接：[下载 Android Studio 和应用工具 - Android 开发者  | Android Developers (google.cn)](https://developer.android.google.cn/studio?hl=ru)

![image-20240419153326987](anaconda的安装.assets/image-20240419153326987.png)

因为之前已经安装过 Android Studio ，就不重复安装了，安装过程可参考网上教程。下面演示创建项目。

1. 找到用户目录下 .gradle 目录中的init.gradle文件，修改 gradle 配置，配置使用阿里云云效 Maven 。

   ![image-20240419154112640](anaconda的安装.assets/image-20240419154112640.png)

   内容替换为（参考自网上教程）：

   ```
   allprojects{
       repositories {
           def ALIYUN_REPOSITORY_URL = 'https://maven.aliyun.com/repository/central'
           def ALIYUN_JCENTER_URL = 'https://maven.aliyun.com/repository/public'
           all { ArtifactRepository repo ->
               if(repo instanceof MavenArtifactRepository){
                   def url = repo.url.toString()
                   if (url.startsWith('https://repo1.maven.org/maven2') || url.startsWith('http://repo1.maven.org/maven2')) {
                       project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_REPOSITORY_URL."
                       remove repo
                   }
                   if (url.startsWith('https://jcenter.bintray.com/') || url.startsWith('http://jcenter.bintray.com/')) {
                       project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_JCENTER_URL."
                       remove repo
                   }
               }
           }
           maven {
               url ALIYUN_REPOSITORY_URL
               url ALIYUN_JCENTER_URL
           }
       }
    
    
       buildscript{
           repositories {
           def ALIYUN_REPOSITORY_URL = 'https://maven.aliyun.com/repository/central'
           def ALIYUN_JCENTER_URL = 'https://maven.aliyun.com/repository/public'
               all { ArtifactRepository repo ->
                   if(repo instanceof MavenArtifactRepository){
                       def url = repo.url.toString()
                       if (url.startsWith('https://repo1.maven.org/maven2') || url.startsWith('http://repo1.maven.org/maven2')) {
                           project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_REPOSITORY_URL."
                           remove repo
                       }
                       if (url.startsWith('https://jcenter.bintray.com/') || url.startsWith('http://jcenter.bintray.com/')) {
                           project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_JCENTER_URL."
                           remove repo
                       }
                   }
               }
               maven {
                   url ALIYUN_REPOSITORY_URL
                   url ALIYUN_JCENTER_URL
               }
           }
       }
   }
   ```

2. 打开 Android Studi ，点击新建 Empty Activity 项目，修改项目名、包名、项目路径等，点击完成。

![image-20240419153609382](anaconda的安装.assets/image-20240419153609382.png)

![image-20240419154422927](anaconda的安装.assets/image-20240419154422927.png)

3. 项目创建完毕，自动进入代码编辑界面

   ![image-20240419154701406](anaconda的安装.assets/image-20240419154701406.png)

4. 点击运行键，运行程序，在模拟器中就能看到相应的APP界面。

   ![image-20240419160836083](anaconda的安装.assets/image-20240419160836083.png)

   ![image-20240419160744824](anaconda的安装.assets/image-20240419160744824.png)

# anaconda的安装

## 访问anaconda官网下载anaconda

anaconda官网链接：[Unleash AI Innovation and Value | Anaconda](https://www.anaconda.com/)

![image-20240419102520937](anaconda的安装.assets/image-20240419102520937.png)

点击 Free Download 进入新页面，点击 Skip registration 直接进入下载页。

![image-20240419102539787](anaconda的安装.assets/image-20240419102539787.png)

进入下载页，选择需要下载的版本进行下载。

![image-20240419102601182](anaconda的安装.assets/image-20240419102601182.png)

## 安装anaconda

1. 点击 Next 。

![image-20240419102615409](anaconda的安装.assets/image-20240419102615409.png)

2. 点击 I Agree 。

![image-20240419102627143](anaconda的安装.assets/image-20240419102627143.png)

3. 选择 Just Me ，点击 Next 。

![image-20240419102636995](anaconda的安装.assets/image-20240419102636995.png)

4. 选择安装路径，路径中不要有中文和空格。点击 Next 。

![image-20240419102649245](anaconda的安装.assets/image-20240419102649245.png)

5. 勾选图中的第一个和第三个选项，允许创建开始菜单的快捷键、使用 Anaconda 作为默认 Python 环境，点击 Install 。

![image-20240419102658920](anaconda的安装.assets/image-20240419102658920.png)

6. 等待安装完毕，然后点击 Next 。

![image-20240419102712841](anaconda的安装.assets/image-20240419102712841.png)

7. 点击 Next 。

![image-20240419102722031](anaconda的安装.assets/image-20240419102722031.png)

8. 图中两个选项可以不选，点击 Finish。

![image-20240419102733401](anaconda的安装.assets/image-20240419102733401.png)

## 验证是否安装成功

- 开 始 → Anaconda3 （64-bit ） → Anaconda Navigator(anaconda3)，启动成功说明安装成功。

  ![image-20240419102743453](anaconda的安装.assets/image-20240419102743453.png)

  ![image-20240419102801866](anaconda的安装.assets/image-20240419102801866.png)

- 开始 → Anaconda3（64-bit）→ 右键点击Anaconda Prompt→ 以“管理员身份运行” ，在Anaconda Prompt中输入 conda

list ，可以查看已经安装的包名和版本号。若结果可以正常显示，则说明安装成功。

![image-20240419102801866](anaconda的安装.assets/image-20240419102801866.png)

![image-20240419145801192](anaconda的安装.assets/image-20240419145801192.png)

# Jupyter Notebook 的基本配置与使用

1. 在 Anaconda Navigator 中点击 Notebook 下的 launch 按钮启动 Notebook 。

![image-20240419150158745](anaconda的安装.assets/image-20240419150158745.png)

2. 启动后就进入了 Jupyter Notebook 网页，此时默认的Files列出了用户文件夹的项目

![image-20240419150700002](anaconda的安装.assets/image-20240419150700002.png)

3. 更改默认加载的目录（如有需要）

   - 在 Anaconda Powershell Prompt 中输入 jupyter notebook --generate-config 生成配置文件。

     ![image-20240419151544592](anaconda的安装.assets/image-20240419151544592.png)

   - 找到上图中给定路径的配置文件，打开该文件，查找 “ notebook_dir ” 所在位置。

     ![image-20240419151717560](anaconda的安装.assets/image-20240419151717560.png)

   - 将这一行的注释符 “ # ” 删掉，然后将等于号后面的改为需要设置的默认路径，保存后关闭。

     ![image-20240419151823558](anaconda的安装.assets/image-20240419151823558.png)

   - 打开开始菜单中 Jupyter Notebook 快捷方式的所在位置

     ![image-20240419151929850](anaconda的安装.assets/image-20240419151929850.png)

   - 右键快捷方式打开属性，修改快捷方式的目标，将后面引号部分的内容删去，然后点击确定。

     ![image-20240419152108406](anaconda的安装.assets/image-20240419152108406.png)

   - 重新运行 Jupyter Notebook ， 看到默认目录已切换。

     ![image-20240419152330861](anaconda的安装.assets/image-20240419152330861.png)

4. 新建 Notebook 。

   ![image-20240419152453663](anaconda的安装.assets/image-20240419152453663.png)

   ![image-20240419152558908](anaconda的安装.assets/image-20240419152558908.png)

5. 输入简单的 Python 语句进行测试，然后点击运行，看到输出语句。

   ![image-20240419152712133](anaconda的安装.assets/image-20240419152712133.png)

   ![image-20240419152806573](anaconda的安装.assets/image-20240419152806573.png)

   ![image-20240419152822198](anaconda的安装.assets/image-20240419152822198.png)

# vscode 的安装及使用

1. 在 Anacoda Navigator 中安装 VS Code（或者自己通过VS Code官网下载），然后点击Launch。

![image-20240419161045663](anaconda的安装.assets/image-20240419161045663.png)

2. 新建笔记本。

   ![image-20240419161246312](anaconda的安装.assets/image-20240419161246312.png)

3. 选择 Anaconda 内核。

   ![image-20240419161537056](anaconda的安装.assets/image-20240419161537056.png)

   ![image-20240419161601178](anaconda的安装.assets/image-20240419161601178.png)

   ![image-20240419161618218](anaconda的安装.assets/image-20240419161618218.png)

   

4. 编写代码。

   ![image-20240419161705005](anaconda的安装.assets/image-20240419161705005.png)

5. 点击代码前的运行按钮，运行程序，查看输出。

   ![image-20240419161805157](anaconda的安装.assets/image-20240419161805157.png)