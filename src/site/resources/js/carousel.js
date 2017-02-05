/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

$(function () {
    $('.left.carousel-control').attr('data-slide', 'prev');
    $('.right.carousel-control').attr('data-slide', 'next');
    $('.carousel').each(function () {
        var carousel = $(this);
        var i = 0;
        carousel.find('.carousel-indicators li')
            .attr('data-target', '#' + this.id)
            .attr('data-slide-to', function () { return i++; });
        carousel.carousel();
    });
});
