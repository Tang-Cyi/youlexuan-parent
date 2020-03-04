 //控制层 
app.controller('addressController' ,function($scope,$controller   ,addressService){	
	
	// $controller('baseController',{$scope:$scope});//继承
	$scope.selecteAddress=function(address){
		$scope.entity=address;
	}
	//保存
	$scope.saveAddress=function(){
		var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity ); //修改
		}else{
			serviceObject=addressService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
					$scope.findAddressList();//重新加载
				}else{
					alert(response.message);
				}
			}
		);
	}


	//删除
	$scope.deleAddress=function(id){
		//获取选中的复选框
		var state=confirm("你确定要删除吗？");
		if (state){
			addressService.dele( id ).success(
				function(response){
					if(response.success){
						$scope.findAddressList();//刷新列表
						$scope.selectIds=[];
					}
					alert(response.message);
				}
			);
		}

	}

	$scope.selectPayType = function (type) {
		$scope.order.paymentType = type;
	}
	//记录被选中的地址
	$scope.selectAddress = function (address) {
		$scope.address = address;
	}

	//判断是否是被选中的地址
	$scope.isSelectedAddress = function (address) {
		if (address == $scope.address) {
			return true;
		} else {
			return false;
		}
	}
	//获取地址列表
	$scope.findAddressList = function () {
		addressService.findAddressList().success(function (response) {
			$scope.addressList = response;
			for (var i = 0; i < $scope.addressList.length; i++) {
				var address = $scope.addressList[i];
				if (address.isDefault == '1') {
					$scope.address = $scope.addressList[i];
					break;
				}
			}
		});
	}
	

    
});	