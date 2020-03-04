//广告控制层（运营商后台）
app.controller("indexController",function($scope,contentService){

    $scope.contentList=[];//categoryId作为数组索引，广告集合作为值

    $scope.findByCategoryId=function(categoryId){
        contentService.findByCategoryId(categoryId).success(
            function(response){
                $scope.contentList[categoryId]=response;
            }
        );
    }
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }



});
