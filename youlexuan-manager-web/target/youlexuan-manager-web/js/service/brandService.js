//定义Service层（创建自定义服务），将和后端交互的部分封装到自定义服务
app.service('brandService',function ($http) {
    this.findAll =function () {
        return $http.get("../brand/findAll.do");
    }
    this.findPage=function (page,rows) {
        return $http.get("../brand/findPage.do?pageNum=" + page + "&pageSize=" + rows)
    }
    this.search=function (page,rows,searchEntity) {
        return $http.post("../brand/search.do?pageNum=" + page + "&pageSize=" + rows, searchEntity)
    }
    this.save=function (entity) {
        return  $http.post("../brand/add.do", entity);
    }
    this.update=function (entity) {
        return  $http.post("../brand/update.do", entity);
    }
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id=" + id)
    }
    this.delete=function (selectIds) {
        return  $http.get("../brand/delete.do?ids=" + selectIds);
    }

});