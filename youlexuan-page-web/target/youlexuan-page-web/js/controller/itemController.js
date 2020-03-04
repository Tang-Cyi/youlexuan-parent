//商品详细页（控制层）
app.controller('itemController',function($scope,$http){
	//添加商品到购物车
	$scope.addToCart=function(){
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(function (response) {
			if (response.success){
				//添加成功则跳转
				location.href='http://localhost:9107/cart.html';
				alert('商品:'+$scope.sku.id+'添加成功！');

			}else {
				alert(response.message)
			}
		})
	}

	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	//记录用户选择的规格，被选中的选项就会在该对象中
	$scope.specificationItems={};
	//点击某个规格的时候，添加至specificationItems
	$scope.selectSpecification=function(name,value){	
		$scope.specificationItems[name]=value;
		searchSku();//读取sku
	}	
	//判断某规格选项是否被用户选中，如果该规格在specificationItems，说明被选中
	$scope.isSelect=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}		
	}
	//加载默认的sku,显示价格和title
	$scope.loadSku=function(){
		 $scope.sku=skuList[0];
		//  要通过JSON转换！
		 $scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}


	//匹配两个对象
	matchObject=function(map1,map2){		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}			
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}			
		}
		return true;		
	}

	//查询SKU
	searchSku=function(){
		for(var i=0;i<skuList.length;i++ ){
			var sku=skuList[i];
			if( matchObject(sku.spec ,$scope.specificationItems ) ){
				$scope.sku=sku;
				return ;
			}
 		}	
			
	}



});

