const wrapper = document.querySelector(".wrapper");
const backBtn = document.querySelector(".back-btn");

const toggleScreen = () => {
    wrapper.classList.toggle("show-item");
};

backBtn.addEventListener("click", toggleScreen);

const addReviewBtn = document.querySelector(".add-review-btn");
const addReviewForm = document.querySelector(".add-review");
const blackBackdrop = document.querySelector(".black-backdrop");



const toggleAddReviewForm = () => {
    addReviewForm.classList.toggle("active");
    blackBackdrop.classList.toggle("active");
    addReviewBtn.classList.toggle("active");
};

addReviewBtn.addEventListener("click", toggleAddReviewForm);
blackBackdrop.addEventListener("click", toggleAddReviewForm);

const cartBtn = document.querySelector(".cart-btn");
const closeCart = document.querySelector('.close');
const body = document.querySelector("body");

cartBtn.addEventListener("click", () => {
    body.classList.toggle('show-cart');
});

closeCart.addEventListener("click", () => {
    body.classList.toggle('show-cart');
})

