const wrapper = document.querySelector(".wrapper");
const backBtn = document.querySelector(".back-btn");

const toggleScreen = () => {
    wrapper.classList.toggle("show-item");
};

backBtn.addEventListener("click", toggleScreen);


const cartBtn = document.querySelector(".cart-btn");
const cartTab = document.querySelector(".cartTab");
const blackBackdrop = document.querySelector(".black-backdrop");

const toggleCartForm = () => {
    cartBtn.classList.toggle("active");
    blackBackdrop.classList.toggle("active");
    cartTab.classList.toggle("active");
};

cartBtn.addEventListener("click", toggleCartForm);
blackBackdrop.addEventListener("click", toggleCartForm);


let orderItems = [];
let orderItemsHTML = document.querySelector('.orderItems');

const addToCart = (element) => {
    let itemId = parseInt(element.getAttribute("itemId"));
    let item = findItemById(itemId);
    console.log(item);
    let itemPos = orderItems.findIndex((value) => value.itemId === itemId);
    if (orderItems.length <= 0) {
        orderItems = [{
            itemId: itemId,
            count: 1
        }];
    }
    else if (itemPos < 0) {
        orderItems.push({
            itemId: itemId,
            count: 1
        });
    }
    else {
        orderItems[itemPos].count += 1;
    }
    console.log(orderItems);
    addOrderItemsToHtml();
}

const addOrderItemsToHtml = () => {
    orderItemsHTML.innerHTML = '';
    let totalQuantity = 0;
    if (orderItems.length > 0) {
        orderItems.forEach(orderItem => {
            totalQuantity += orderItem.count;
            let newOrderItem = document.createElement('div');
            newOrderItem.classList.add('item');
            newOrderItem.dataset.id = orderItem.itemId;
            let item = findItemById(orderItem.itemId);
            newOrderItem.innerHTML =
                `
                    <div class="name">
                        ${item.name}
                    </div>
                    <div class="count">
                        <span class="minus">-</span>
                        <span>${orderItem.count}</span>
                        <span class="plus">+</span>
                    </div>
                    <div class="totalPrice">${item.price * orderItem.count}원</div>
                `;
            orderItemsHTML.appendChild(newOrderItem);
        })
    }
}

orderItemsHTML.addEventListener("click", (event) => {
    let clickedPosition = event.target;
    if (clickedPosition.classList.contains('minus') || clickedPosition.classList.contains('plus')) {
        let itemId = clickedPosition.parentElement.parentElement.dataset.id;
        let type = 'minus';
        if (clickedPosition.classList.contains('plus')) {
            type = 'plus';
        }
        changeQuantity(itemId, type);
    }
})

const changeQuantity = (itemId, type) => {
    let itemPos = orderItems.findIndex((value) => value.itemId == itemId);
    if (itemPos >= 0) {
        switch (type) {
            case 'plus':
                orderItems[itemPos].count = orderItems[itemPos].count + 1;
                break;
            default:
                let valueChange = orderItems[itemPos].count - 1;
                if (valueChange > 0) {
                    orderItems[itemPos].count = valueChange;
                }
                else {
                    orderItems.splice(itemPos, 1);
                }
                break;
        }
    }
    addOrderItemsToHtml();
}