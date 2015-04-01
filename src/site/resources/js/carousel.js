!function ($) {
    $(function () {
        $('.carousel').carousel();
        $('.left.carousel-control').on("click", function () {
            $('.carousel').carousel('prev');
        });
        $('.right.carousel-control').on("click", function () {
            $('.carousel').carousel('next');
        })
    })
}(window.jQuery);
