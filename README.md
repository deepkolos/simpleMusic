# SimpleMusic

> 网易云音乐高仿(部分实现)\
> 安卓大作业\
> 顺便接触强类型语言, 接触类的使用, 以便后期转入TS\
> 接触安卓组件开发方式及其行为, 还有其资源管理

虽然目的是为了给前端添加些原生的组件开发经验, 还有资源管理策略, 这样的理由有点奇怪, 但是不可否认目前原生的组件无论在行为定义上面还有资源管理策略方面, 都是比前端的好很多很多, 更加加鲁棒, 不过自己也有点小怀疑, 不知道这个要不要写到简历里面? 但是自己毕竟还是这个学期的心血. 

但是实践过之后又发现, 可以搬迁到前端的并不多, 目前我认为安卓的布局方式更加合理, 前端的布局其实是从文档布局演变过来的, UI布局会有不少那种"另类使用". 但是在一些点缀样式比如圆角, 则麻烦很多, 样式方面还是前端强大. 安卓则强大在自定义方面, 每个View都支持paint定义(canvas操作), layout定义, 所以复杂组件的实现更加高效.

资源管理抽方面, 安卓粒度更细, 感觉最强大的是drawable和anim, drawable可以有状态和具体图片的绑定, 还有尺量图片定义等等, 这些的复用程度都很高. 前端的组件方式资源粒度的抽象则没有那么细. 资源加载方面比如图片加载, 则需要手动管理bitmap, 使用LRUCache和DiskLRUCache, 前端则无需这方面的考虑.

后面想去了解谷歌出的Angular了~\
感触相对粗糙, 可能有不少出错的地方, 请谅解和指点~

### 实现功能

0.歌单创建/删除/收藏\
1.歌单添加/删除歌曲\
2.歌曲播放\
3.推荐列表

### 目录说明

<img src="https://s1.ax1x.com/2018/06/23/P98E7t.png" width = "300" alt="" style="display:inline-block;" >

### 运行截图

<div>
  <img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91VKA.png" alt="P91VKA.png" border="0">
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P9112Q.png" alt="P9112Q.png" border="0">
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91nVP.png" alt="P91nVP.png" border="0">
<!-- <img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91ebt.png" alt="P91ebt.png" border="0"> -->
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91ZDI.png" alt="P91ZDI.png" border="0">
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91QPS.png" alt="P91QPS.png" border="0">
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91uUf.png" alt="P91uUf.png" border="0">
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91K58.png" alt="P91K58.png" border="0">
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P913vj.png" alt="P913vj.png" border="0">
<img width = "250" alt="" style="display:inline-block;" src="https://s1.ax1x.com/2018/06/23/P91l8g.png" alt="P91l8g.png" border="0">
</div>

### 实现的各类widget

<img width = "350" src="https://s1.ax1x.com/2018/06/23/P98U9U.png" alt="P98U9U.png" border="0" />

### 感谢

[PonyMusic](https://github.com/wangchenyan/PonyMusic) blur实现, 部分素材使用\
[NeteaseCloudMusic](https://github.com/yanunon/NeteaseCloudMusic) 接口参考

<img src="https://s1.ax1x.com/2018/06/23/P98A0I.png" width = "650" alt="" style="display:inline-block;" >