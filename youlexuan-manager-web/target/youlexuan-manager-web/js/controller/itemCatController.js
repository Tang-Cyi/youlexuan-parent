//商品类目控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.add = function () {
        itemCatService.add($scope.entity).success(function (response) {
            if (response.success) {
                $scope.findItemCatByParentId($scope.parentId)
            } else {
                alert(response.message)
            }
        });
    }
    //查询所有模板数据
    $scope.findAllTypeTemplate = function () {
        typeTemplateService.findAll().success(function (response) {
            $scope.typeTemplateList = response;
        })
    }


    $scope.grade = 1;
    //更新grade级别
    $scope.setGrade = function (newGrade) {
        $scope.grade = newGrade;
    }
    $scope.myLocation="顶层分类";
    $scope.setMyLocation=function(name){
        $scope.myLocation+='>>'+name;
    }
    $scope.parentId = 0;


    $scope.findItemCatByParentId = function (pid) {
        $scope.parentId = pid
        itemCatService.findItemCatByParentId(pid).success(function (response) {
            $scope.list = response;
        });
    }
    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }
    //批量删除
    $scope.deleteItemCat = function () {
        //获取选中的复选框
        itemCatService.deleteItemCat($scope.selectIds).success(
            function (response) {
                alert(response.message);
                //重新加载页面
                $scope.selectIds = [];
                $scope.findItemCatByParentId($scope.parentId);
            }
        );
    }
    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

});	