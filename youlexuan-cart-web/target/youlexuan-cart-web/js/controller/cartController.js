app.controller('cartController', function ($scope, cartService) {

    $scope.submitOrder = function () {
        // 将收件人等信息赋值到order
        $scope.order.receiverAreaName = $scope.address.address;//地址赋值
        $scope.order.receiverMobile = $scope.address.mobile;//手机赋值
        $scope.order.receiver = $scope.address.contact;//联系人赋值
        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success) {
                //订单提交成功 页面跳转
                //在线支付 跳转到 pay.html 还是货到付款
                if ($scope.order.paymentType=='1'){
                    location.href="pay.html";
                }else {
                    location.href="order-success.html";
                }
            } else {
                //提交订单失败
                alert(response.message);
            }
        })
    }
    //用来记录支付方式  收件人联系方式 收货地址 总价等
    $scope.order = {paymentType: '1',};
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
        cartService.findAddressList().success(function (response) {
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


    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            //调用sum方法
            $scope.totalValue = cartService.sum($scope.cartList);
        })
    }
    //cart数量增加
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(function (response) {
            if (response.success) {
                // 添加成功重新加载
                $scope.findCartList();

            } else {
                alert(response.message);
            }
        })
    }

});