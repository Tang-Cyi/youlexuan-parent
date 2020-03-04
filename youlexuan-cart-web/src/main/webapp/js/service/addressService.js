//服务层
app.service('addressService',function($http){
	    	

	//增加 
	this.add=function(entity){
		return  $http.post('../address/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../address/update.do',entity );
	}
	//删除
	this.dele=function(id){
		return $http.get('../address/delete.do?ids='+id);
	}

	this.addAddress=function () {
		return $http.post('/address/add.do',order);
	}
	//获取地址列表
	this.findAddressList=function(){
		return $http.get('/address/findListByLoginUser.do');
	}

});