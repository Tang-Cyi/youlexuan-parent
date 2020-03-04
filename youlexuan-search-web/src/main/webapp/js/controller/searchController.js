app.controller('searchController',function ($scope,searchService,$location) {
    //获取路径上的参数
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=$location.search()['keywords']
        $scope.search();
    }

    $scope.resultMap={};
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
        });
    }
    $scope.searchMap={keywords:'',category:'',brand:'',spec:{},price:'',sortField:'',sort:'' };
    //添加搜索项
    $scope.addSearchItem=function(key,value){
        if(key=='category' || key=='brand' || key=='price'){//如果点击的是分类或者是品牌
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }

    //点击x号，删除面包屑
    $scope.removeSearchItem=function(key ){
        if(key=='category' || key=='brand' || key=='price'){//如果点击的是分类或者是品牌
            $scope.searchMap[key]='';
        }else{
           delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    //设置排序字段
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search()
    }
    //判断关键词是否是品牌
    $scope.keywordsIsBrand=function () {
        var  brandList=$scope.resultMap.brandList;
        var  keywords=$scope.searchMap.keywords;
        for (var i=0;i<brandList.length;i++){
            if (keywords.indexOf(brandList[i].text)>=0){ //当前品牌是否包含在关键词中
                return true;
            }
        }
        return false;
    }
});