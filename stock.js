var request = require('request')
var gbk = require('gbk')
var colors = require('colors')  
var util = require('util')
  
colors.setTheme({  
    fall: 'green',rise: 'red'
});  

var config = {
    //stockArr : ['s_sh000001','sz002230','sh600307','sh600844','sz002457','sh600503'],
    stockArr : ['s_sh000001','sh600307','sz002303'],
    url:'http://hq.sinajs.cn/list='
}
var len = config.stockArr.length
for (var i = 0; i < len; i++) {
    config.url += config.stockArr[i]
    if (i < len-1)
        config.url += ','
};
var stocker = {
    run: function(){
        request({url:config.url,encoding: null},function (error, response, gbkBuffer) {
            var content = gbk.toString('utf-8',gbkBuffer);
            if (!error && response.statusCode == 200) {
                stocker.parseRes(content)
            }
        })
    },

    parseRes:function(content){
        var resultArr = content.split(";")
        for (var i = 0; i < resultArr.length; i++) {
            var result = resultArr[i]
            var dataStr = result.substring(result.indexOf("=") + 2, result.length - 1)
            var datas = dataStr.split(',')
            //上证指数
            if(result.indexOf("sh000001") > -1){
                var stockName = datas[0]
                console.log("------------------------------" + stockName + "------------------------------------------")
                if(datas[3] > 0){
                    stocker.riseMsg("当前点数:%s 涨幅:%s  涨跌(点数):%s",datas[1],datas[3] + "%",datas[2])
                }else{
                    stocker.fallMsg("当前点数:%s 跌幅:%s  涨跌(点数):%s",datas[1],datas[3] + "%",datas[2])
                }

                stocker.infoMsg("总成交:%s 手  成交金额: %s",datas[4],datas[5].substring(0,datas[5]))
            }else{
                var stockName = datas[0]
                if(!stockName.trim()) continue
                console.log("------------------------------" + stockName + "------------------------------------------")
                var currentPrice = datas[3];
                var yestodayPrice = datas[2];
                if(currentPrice - yestodayPrice < 0){
                    stocker.fallMsg("当前价:%s  跌幅:%s  今开:%s  昨收:%s",(currentPrice),(((yestodayPrice - currentPrice)/yestodayPrice*100)+"%"),(datas[1]),(yestodayPrice));
                    stocker.fallMsg("最高:%s 最低:%s 买价:%s 卖价:%s",datas[4],datas[5],datas[6],datas[7])
                }else{
                    stocker.riseMsg("当前价:%s  涨幅:%s  今开:%s  昨收:%s",(currentPrice),(((currentPrice - yestodayPrice)/yestodayPrice*100)+"%"),(datas[1]),(yestodayPrice));
                    stocker.riseMsg("最高:%s 最低:%s 买价:%s 卖价:%s",datas[4],datas[5],datas[6],datas[7])
                }

                stocker.infoMsg("总成交:%s 手  成交金额: %s",datas[8],datas[9])
            }
            
        };
    },

    infoMsg:function(){
        stocker.logMsg('info',arguments)    
    },

    riseMsg:function(){
        stocker.logMsg('rise',arguments)
    },

    fallMsg:function(){
        stocker.logMsg('fall',arguments)
    },

    logMsg:function(){

        if(arguments.length < 2)
            return
        var model = arguments[0]
        var args = arguments[1]
        for (var i = 1; i < args.length; i++) {
             if(model === 'rise'){
                args[i] =  args[i].rise
             }else if(model === 'fall'){
                args[i] =  args[i].fall
             }
         }
        console.log.apply(console,args)
    }

}


stocker.run()
setInterval(function() {
    stocker.run()
},5000)