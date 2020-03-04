app.controller('goodsEditController',function ($scope,$controller) {
    
    $controller('baseController',{$scope:$scope});

    $scope.updateSpecAttribute=function($event,name,value){
        var object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems ,'attributeName', name);
        if(object!=null){
            if($event.target.checked ){
                object.attributeValue.push(value);
            }else{
                object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);
                if(object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
                }
            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
    }
    $scope.searchObjectByKey=function(list,key,keyValue){
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }


//创建SKU列表
$scope.createItemList=function(){	
	$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
	var items=  $scope.entity.goodsDesc.specificationItems;	
	for(var i=0;i< items.length;i++){
		$scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );    
	}	
}
//添加列值 
addColumn=function(list,columnName,conlumnValues){
	var newList=[];//新的集合
	for(var i=0;i<list.length;i++){
		var oldRow= list[i];
		for(var j=0;j<conlumnValues.length;j++){
			var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
			newRow.spec[columnName]=conlumnValues[j];
			newList.push(newRow);
		}    		 
	} 		
	return newList;
}


});