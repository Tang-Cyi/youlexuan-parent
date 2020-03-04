 //用户表控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	// $controller('baseController',{$scope:$scope});//继承

	//注册
     $scope.reg=function () {
     //	表单校验
		 if ($scope.entity.password!=$scope.password){
		 	alert("密码不一致！")
			 return ;
		 }
		 userService.add($scope.entity,$scope.smscode).success(function (response) {
		 	alert(response.message)
		 })
	 }
	 $scope.sendCode=function () {

		 if ($scope.entity.phone==null){
     		alert("手机号不能为空！")
			return ;
		}
		 /*var checkedPhone = /^[1][3,4,5,7,8][0-9]{9}$/;
		 if (!checkedPhone.test(pone)) {
			 alert("手机号格式不正确！")
		 }*/
		 userService.sendCode($scope.entity.phone).success(function (response) {
		 	alert(response.message)
		 })
	 }

    
});	