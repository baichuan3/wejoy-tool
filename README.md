# wejoy-tool
a useful tool for topN condition in app dev.

#### contains:
<p>
1 MaxTopN cache,<br>
2 BoxCache,<br>
3 LRUCache,<br>

其中，MaxTopN的特性，
<pre> 
  场景：互联网开发中常见的TopN场景，例如热门的帖子列表，热门的目的地，热门的投票等<br>
  目标：提供高并发场景下的通用排序组件，高性能，LockFree；<br>
  特性：支持key-value对排序，且value可重复，key对应name，value对应weight；
  支持cache的dump和reload；<br> 
  使用说明：涉及到元素更新，需要先copy出副本，在副本上更新，再覆盖回origin，不然可能导致remove失败；<br>
  备注：使用跳表时，由于跳表的随机性，对于插入时按权值排序，删除或查找时按name比对的场景，会以较大的概率失败；解决办法——维护单独的Map，存放node.name到node的映射，contains和remove时都依赖次Map<br>
</pre>
 
 BoxCache特性，
<pre>
  场景：收发件箱场景，uid--> long[]，id本身需有序，以及与此相似的场景；<br>
  特性：支持超过最大值剔除；默认倒序排序：desc；<br>
  Q：为何有了MaxTopN组件后，还要写这个组件；
  A：一切为了节省内存<br>
  备注：高并发时有潜在的性能问题；<br>
<pre> 

