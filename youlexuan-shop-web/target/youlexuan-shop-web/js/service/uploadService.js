//文件上传服务层
app.service("uploadService",function($http){
    this.uploadFile=function(){
        var formData=new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined}, /*angularjs对于post和get请求默认的Content-Type
                                                    header 是application/json。
                                                通过设置‘Content-Type’: undefined，
                                                浏览器会把Content-Type 设置为 multipart/form-data.*/
            transformRequest: angular.identity
        });
    }
});
