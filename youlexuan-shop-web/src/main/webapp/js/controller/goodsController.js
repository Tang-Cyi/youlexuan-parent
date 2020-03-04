//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, uploadService, itemCatService,typeTemplateService) {
    // $controller('baseController', {$scope: $scope});//继承
    $controller('goodsEditController', {$scope: $scope});//继承

    $scope.updateAuditStatus=function(auditStatus){
        goodsService.updateAuditStatus($scope.selectIds,auditStatus).success(function (response) {
            if(response.success){//成功
                $scope.reloadList();//刷新列表
                $scope.selectIds=[];//清空ID集合
            }else{
                alert(response.message);
            }
        })
    }

    $scope.toGoodsEdit=function(){
        location.href='../admin/goods_edit.html';
    }
    $scope.marketableStates = ['','已上架','已下架'];
    $scope.updateMarketable = function(isMarketable){
        goodsService.updateMarketable($scope.selectIds,isMarketable).success(function (response) {
            if(response.success){
                $scope.reloadList();
                $scope.selectIds = [];
            }
        });
    }

    $scope.$watch('searchEntity.auditStatus',function (newValue,oldValue) {
        if (newValue){
            $scope.reloadList();
        }
    })
    $scope.itemCatName=[];//商品分类列表
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(function (response) {
            for (var i=0;i<response.length;i++){
                var c=response[i];
                $scope.itemCatName[c.id]=c.name;
            }
        });
    }
        $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
    //查询一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findItemCatByParentId('0').success(function (response) {
                $scope.itemCat1List = response;
            }
        );
    };
    //1、监听的变量 2、响应函数，新的值 旧的值
    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
    	$scope.itemCat3List=[];
    	if (newValue){
			itemCatService.findItemCatByParentId(newValue).success(function (response) {
				$scope.itemCat2List = response;
			});
		}
	});
	//1、监听的变量 2、响应函数，新的值 旧的值
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		if (newValue){
			itemCatService.findItemCatByParentId(newValue).success(function (response) {
				$scope.itemCat3List = response;
			});
		}
	});
	//1、监听的变量 2、响应函数，新的值 旧的值
	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		if (newValue){
			itemCatService.findOne(newValue).success(function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
			});
		}
	});
	//1、监听的变量 2、响应函数，新的值 旧的值
	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
		if (newValue){
			itemCatService.findOne(newValue).success(function (response) {
				typeTemplateService.findOne(newValue).success(function (response) {
					$scope.typeTemplate=response;
					$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
					// $scope.typeTemplate.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
					$scope.entity.goodsDesc.customAttributeItems= JSON.parse( $scope.typeTemplate.customAttributeItems);//扩展属性
				})
                //重新查询规格选项
                typeTemplateService.findSpecAndOption(newValue).success(function (response) {
                    $scope.specList = response;
                })
			});
		}
	});

    //列表中移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    $scope.entity = {goods: {}, goodsDesc: {itemImages: [],specificationItems:[]},itemList:[]};//定义页面实体结构
    //将当前图片添加到itemImages中
    $scope.pushToItemImages = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    /**
     * 上传图片
     */
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {//如果上传成功，取出url
                $scope.image_entity.url = response.message;//设置文件地址
            } else {
                alert(response.message);
            }
        })
    };


    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(function (response) {
            if (response.success) {
                alert('保存成功');
                $scope.entity = {};
                editor.html('');//保存成功之后，清空富文本编辑器的内容
            } else {
                alert(response.message);
            }
        });
    };
    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
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
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

});	