/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

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
