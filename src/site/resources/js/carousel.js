/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

$(function () {
    $('.left.carousel-control').attr('data-slide', 'prev');
    $('.right.carousel-control').attr('data-slide', 'next');
    $('.carousel').each(function (index) {
        var carousel = $(this);
        var i = 0;
        carousel.find('.carousel-indicators li')
            .attr('data-target', '#' + this.id)
            .attr('data-slide-to', function () { return i++; });
        carousel.carousel();
    });
});
