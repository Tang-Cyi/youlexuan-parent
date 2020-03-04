app.controller('baseController',function ($scope) {

    $scope.selectIds = [];
    $scope.updateSelect = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);//该id在数组的那个位置
            $scope.selectIds.splice(idx, 1)//从数组selectId中idx位置 移除1个元素
        }
    }
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);//该id在数组的那个位置
            $scope.selectIds.splice(idx, 1)//从数组selectId中idx位置 移除1个元素
        }
    }
    $scope.remeberIds=function(id){
       /* var flag1=true;
        var flag2=$scope.selectIds.indexOf(id)!=-1;
        if (!flag2){
            flag1=false;
        }
        $("#selall").prop("checked",flag1);
        return flag2;*/
    }

    $scope.selectAll = function ($event) {
        angular.forEach($scope.list, function (entity) {
            $scope.updateSelect($event,entity.id);
        })
    }

    $scope.reloadList = function () {
        // $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)

    }
    $scope.paginationConf = {
        currentPage: 1,//当前页
        totalItems: 10,//总记录数
        itemsPerPage: 10,//每页显示的条数
        perPageOptions: [10, 20, 50],
        onChange: function () {
            $scope.reloadList();
        },
    }

    $scope.jsonToStr=function (str,key) {
        var arr=JSON.parse(str);
        var result=[];
        for (var i=0;i<arr.length;i++){
            var obj=arr[i];
            result.push(obj[key]);
        }

       /* for (var a in arr) {
            //for循环意义不同，不是普通的for循环！
            result.push(a[key]);
        }*/
        return result.toString();
    }

});