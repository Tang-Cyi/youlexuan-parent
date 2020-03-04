app.controller('brandController', function ($scope,brandService,$controller) {

    $controller('baseController',{$scope:$scope});

    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(function (response) {
            if (response.success) {
                $scope.reloadList();
                $scope.selectIds = [];
            } else {
                alert(response.message);
            }
        })
    }

    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    }
    $scope.save = function () {
        if ($scope.entity.id != null) {
            brandService.update($scope.entity).success(function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            });
        }else {
            brandService.save($scope.entity).success(function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            });
        }

    }
    /*查询所有品牌*/
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        })
    }
    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    }
    /*待条件的分页查询*/
    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }
})