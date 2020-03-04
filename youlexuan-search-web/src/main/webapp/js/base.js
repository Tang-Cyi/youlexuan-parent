// 创建base.js （不带分页组件）
// 定义模块:
var app = angular.module("youlexuan",[]);
/*$sce服务写成过滤器 支持html字符串显示，如果没有这个，在后台返回来含有html代码的会原样输出，加了这个可以显示其样式*/
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);
