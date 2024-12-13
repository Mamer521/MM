**flinkcdc读取依赖问题**:
    flinkcdc的版本不同情况,因为版本不同,所以导入依赖的要自己导入即可
导包之前:
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
解决后的包:
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import com.ververica.cdc.connectors.mysql.MySQLSource;

![fd93954af55f11cee3e204ac5971c5f0](https://github.com/user-attachments/assets/db5f4bd3-56e7-4cee-98db-aa184f5e3561)![image](https://github.com/user-attachments/assets/5a773088-58fc-4d3b-8344-e124129d1289)### 数据倾斜问题

​	例子:如果现在有两个人数两个班级多少人,共有30人,一个班级是3个人,另一个班级是27人,那么这种严重的情况就发生了严重的倾斜;

如果在sql中数据倾斜一般发生在group by ,distinct,join,根据某个字段做统计等;

优惠方案:

```hivesql中的数据倾斜优化
with t1 as (
    select userid,
           order_no,
           concat(region,'-',rand()) as region,
           product_no,
           color_no,
           sale_amount,
           ts
    from date_east
),
    t2 as (
        select region,
               count(1) as cnt
        from t1
        group by region
    ),
    t3 as (
        select region,
               substr(region,1,2) as re,
               cnt
        from t2
    )
select re,
       count(1) as cnt
from t3
group by re;
```

**解决思路一**:如果是直接跟据一个地区(region)去分组的话,会出现一个数据清洗,解决数据倾斜,我们可以先用一个concat()函数进行一个地区和随机数的拼接,先将数据打散,然后进行分组统计,接着将我们原有的地区数据取出来,重新做一个查询进行统计,可以避免一个数据倾**斜**


**解决思路二**:增大map和reduce的数量

```增大map和reduce的数量进行优化
**set mapred.map.tasks=20;**
**set mapred.min.split.size=100000000**;


