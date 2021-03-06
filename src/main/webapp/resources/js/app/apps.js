/*   
Template Name: Color Admin - Responsive Admin Dashboard Template build with Twitter Bootstrap 3.3.5
Version: 1.8.0
Author: Sean Ngu
Website: http://www.seantheme.com/color-admin-v1.8/admin/
*/
var handleSlimScroll = function() {
    "use strict";
    $("[data-scrollbar=true]").each(function() {
        generateSlimScroll($(this))
    })
},
generateSlimScroll = function(e) {
    var a = $(e).attr("data-height");
    a = a ? a: $(e).height();
    var t = {
        height: a,
        alwaysVisible: !0
    };
    /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ? ($(e).css("height", a), $(e).css("overflow-x", "scroll")) : $(e).slimScroll(t)
},
handleSidebarMenu = function() {
    "use strict";
    $(".sidebar .nav > .has-sub > a").click(function() {
        var e = $(this).next(".sub-menu"),
        a = ".sidebar .nav > li.has-sub > .sub-menu";
        0 === $(".page-sidebar-minified").length && ($(a).not(e).slideUp(250,
        function() {
            $(this).closest("li").removeClass("expand")
        }), $(e).slideToggle(250,
        function() {
            var e = $(this).closest("li");
            $(e).hasClass("expand") ? $(e).removeClass("expand") : $(e).addClass("expand")
        }))
    }),
    $(".sidebar .nav > .has-sub .sub-menu li.has-sub > a").click(function() {
        if (0 === $(".page-sidebar-minified").length) {
            var e = $(this).next(".sub-menu");
            $(e).slideToggle(250)
        }
    })
},
handleMobileSidebarToggle = function() {
    var e = !1;
    $(".sidebar").on("click touchstart",
    function(a) {
        0 !== $(a.target).closest(".sidebar").length ? e = !0 : (e = !1, a.stopPropagation())
    }),
    $(document).on("click touchstart",
    function(a) {
        0 === $(a.target).closest(".sidebar").length && (e = !1),
        a.isPropagationStopped() || e === !0 || ($("#page-container").hasClass("page-sidebar-toggled") && (e = !0, $("#page-container").removeClass("page-sidebar-toggled")), $("#page-container").hasClass("page-right-sidebar-toggled") && (e = !0, $("#page-container").removeClass("page-right-sidebar-toggled")))
    }),
    $("[data-click=right-sidebar-toggled]").click(function(a) {
        a.stopPropagation();
        var t = "#page-container",
        i = "page-right-sidebar-collapsed";
        i = $(window).width() < 979 ? "page-right-sidebar-toggled": i,
        $(t).hasClass(i) ? $(t).removeClass(i) : e !== !0 ? $(t).addClass(i) : e = !1,
        $(window).width() < 480 && $("#page-container").removeClass("page-sidebar-toggled")
    }),
    $("[data-click=sidebar-toggled]").click(function(a) {
        a.stopPropagation();
        var t = "page-sidebar-toggled",
        i = "#page-container";
        $(i).hasClass(t) ? $(i).removeClass(t) : e !== !0 ? $(i).addClass(t) : e = !1,
        $(window).width() < 480 && $("#page-container").removeClass("page-right-sidebar-toggled")
    })
},
handleSidebarMinify = function() {
    $("[data-click=sidebar-minify]").click(function(e) {
        e.preventDefault();
        var a = "page-sidebar-minified",
        t = "#page-container";
        $('#sidebar [data-scrollbar="true"]').css("margin-top", "0"),
        $("#sidebar [data-scrollbar=true]").stop(),
        $(t).hasClass(a) ? ($(t).removeClass(a), $(t).hasClass("page-sidebar-fixed") ? (0 !== $("#sidebar .slimScrollDiv").length && ($('#sidebar [data-scrollbar="true"]').slimScroll({
            destroy: !0
        }), $('#sidebar [data-scrollbar="true"]').removeAttr("style")), generateSlimScroll($('#sidebar [data-scrollbar="true"]')), $("#sidebar [data-scrollbar=true]").trigger("mouseover")) : /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) && (0 !== $("#sidebar .slimScrollDiv").length && ($('#sidebar [data-scrollbar="true"]').slimScroll({
            destroy: !0
        }), $('#sidebar [data-scrollbar="true"]').removeAttr("style")), generateSlimScroll($('#sidebar [data-scrollbar="true"]')))) : ($(t).addClass(a), /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ? ($('#sidebar [data-scrollbar="true"]').css("margin-top", "0"), $('#sidebar [data-scrollbar="true"]').css("overflow", "visible")) : ($(t).hasClass("page-sidebar-fixed") && ($('#sidebar [data-scrollbar="true"]').slimScroll({
            destroy: !0
        }), $('#sidebar [data-scrollbar="true"]').removeAttr("style")), $("#sidebar [data-scrollbar=true]").trigger("mouseover"))),
        $(window).trigger("resize")
    })
},
handlePageContentView = function() {
    "use strict";
    $.when($("#page-loader").addClass("hide")).done(function() {
        $("#page-container").addClass("in")
    })
},
handlePanelAction = function() {
    "use strict";
    $("[data-click=panel-remove]").hover(function() {
        $(this).tooltip({
            title: "Remove",
            placement: "bottom",
            trigger: "hover",
            container: "body"
        }),
        $(this).tooltip("show")
    }),
    $("[data-click=panel-remove]").click(function(e) {
        e.preventDefault(),
        $(this).tooltip("destroy"),
        $(this).closest(".panel").remove()
    }),
    $("[data-click=panel-collapse]").hover(function() {
        $(this).tooltip({
            title: "收缩 / 展开",
            placement: "bottom",
            trigger: "hover",
            container: "body"
        }),
        $(this).tooltip("show")
    }),
    $("[data-click=panel-collapse]").click(function(e) {
        e.preventDefault(),
        $(this).closest(".panel:not(.dataTables_processing)").find(".panel-body").slideToggle()
    }),
    $("[data-click=panel-reload]").hover(function() {
        $(this).tooltip({
            title: "Reload",
            placement: "bottom",
            trigger: "hover",
            container: "body"
        }),
        $(this).tooltip("show")
    }),
    $("[data-click=panel-reload]").click(function(e) {
        e.preventDefault();
        var a = $(this).closest(".panel");
        if (!$(a).hasClass("panel-loading")) {
            var t = $(a).find(".panel-body"),
            i = '<div class="panel-loader"><span class="spinner-small"></span></div>';
            $(a).addClass("panel-loading"),
            $(t).prepend(i),
            setTimeout(function() {
                $(a).removeClass("panel-loading"),
                $(a).find(".panel-loader").remove()
            },
            2e3)
        }
    }),
    $("[data-click=panel-expand]").hover(function() {
        $(this).tooltip({
            title: "展开 / 收缩",
            placement: "bottom",
            trigger: "hover",
            container: "body"
        }),
        $(this).tooltip("show")
    }),
    $("[data-click=panel-expand]").click(function(e) {
        e.preventDefault();
        var a = $(this).closest(".panel:not(.dataTables_processing)"),
        t = $(a).find(".panel-body"),
        i = 40;
        if (0 !== $(t).length) {
            var l = $(a).offset().top,
            n = $(t).offset().top;
            i = n - l
        }
        if ($("body").hasClass("panel-expand") && $(a).hasClass("panel-expand")) $("body, .panel:not(.dataTables_processing)").removeClass("panel-expand"),
        $(".panel:not(.dataTables_processing)").removeAttr("style"),
        $(t).removeAttr("style");
        else if ($("body").addClass("panel-expand"), $(this).closest(".panel:not(.dataTables_processing)").addClass("panel-expand"), 0 !== $(t).length && 40 != i) {
            var s = 40;
            $(a).find(" > *").each(function() {
                var e = $(this).attr("class");
                "panel-heading" != e && "panel-body" != e && (s += $(this).height() + 30)
            }),
            40 != s && $(t).css("top", s + "px")
        }
        $(window).trigger("resize")
    })
},
handleDraggablePanel = function() {
    "use strict";
    var e = $(".panel").parent("[class*=col]"),
    a = ".panel-heading",
    t = ".row > [class*=col]";
    $(e).sortable({
        handle: a,
        connectWith: t,
        stop: function(e, a) {
            a.item.find(".panel-title").append('<i class="fa fa-refresh fa-spin m-l-5" data-id="title-spinner"></i>'),
            handleSavePanelPosition(a.item)
        }
    })
},
handelTooltipPopoverActivation = function() {
    "use strict";
    $("[data-toggle=tooltip]").tooltip(),
    $("[data-toggle=popover]").popover()
},
handleScrollToTopButton = function() {
    "use strict";
    $(document).scroll(function() {
        var e = $(document).scrollTop();
        e >= 200 ? $("[data-click=scroll-top]").addClass("in") : $("[data-click=scroll-top]").removeClass("in")
    }),
    $("[data-click=scroll-top]").click(function(e) {
        e.preventDefault(),
        $("html, body").animate({
            scrollTop: $("body").offset().top
        },
        500)
    })
},
handleThemePageStructureControl = function() {
    if ($.cookie && $.cookie("theme")) {
        0 !== $(".theme-list").length && ($(".theme-list [data-theme]").closest("li").removeClass("active"), $('.theme-list [data-theme="' + $.cookie("theme") + '"]').closest("li").addClass("active"));
        var e = "assets/css/theme/" + $.cookie("theme") + ".css";
        $("#theme").attr("href", e)
    }
    $.cookie && $.cookie("sidebar-styling") && 0 !== $(".sidebar").length && "grid" == $.cookie("sidebar-styling") && ($(".sidebar").addClass("sidebar-grid"), $('[name=sidebar-styling] option[value="2"]').prop("selected", !0)),
    $.cookie && $.cookie("header-styling") && 0 !== $(".header").length && "navbar-inverse" == $.cookie("header-styling") && ($(".header").addClass("navbar-inverse"), $('[name=header-styling] option[value="2"]').prop("selected", !0)),
    $.cookie && $.cookie("content-gradient") && 0 !== $("#page-container").length && "enabled" == $.cookie("content-gradient") && ($("#page-container").addClass("gradient-enabled"), $('[name=content-gradient] option[value="2"]').prop("selected", !0)),
    $.cookie && $.cookie("content-styling") && 0 !== $("body").length && "black" == $.cookie("content-styling") && ($("body").addClass("flat-black"), $('[name=content-styling] option[value="2"]').prop("selected", !0)),
    $(".theme-list [data-theme]").live("click",
    function() {
        var e = "assets/css/theme/" + $(this).attr("data-theme") + ".css";
        $("#theme").attr("href", e),
        $(".theme-list [data-theme]").not(this).closest("li").removeClass("active"),
        $(this).closest("li").addClass("active"),
        $.cookie("theme", $(this).attr("data-theme"))
    }),
    $(".theme-panel [name=header-styling]").live("change",
    function() {
        var e = 1 == $(this).val() ? "navbar-default": "navbar-inverse",
        a = 1 == $(this).val() ? "navbar-inverse": "navbar-default";
        $("#header").removeClass(a).addClass(e),
        $.cookie("header-styling", e)
    }),
    $(".theme-panel [name=sidebar-styling]").live("change",
    function() {
        2 == $(this).val() ? ($("#sidebar").addClass("sidebar-grid"), $.cookie("sidebar-styling", "grid")) : ($("#sidebar").removeClass("sidebar-grid"), $.cookie("sidebar-styling", "default"))
    }),
    $(".theme-panel [name=content-gradient]").live("change",
    function() {
        2 == $(this).val() ? ($("#page-container").addClass("gradient-enabled"), $.cookie("content-gradient", "enabled")) : ($("#page-container").removeClass("gradient-enabled"), $.cookie("content-gradient", "disabled"))
    }),
    $(".theme-panel [name=content-styling]").live("change",
    function() {
        2 == $(this).val() ? ($("body").addClass("flat-black"), $.cookie("content-styling", "black")) : ($("body").removeClass("flat-black"), $.cookie("content-styling", "default"))
    }),
    $(".theme-panel [name=sidebar-fixed]").live("change",
    function() {
        1 == $(this).val() ? (2 == $(".theme-panel [name=header-fixed]").val() && (alert("Default Header with Fixed Sidebar option is not supported. Proceed with Fixed Header with Fixed Sidebar."), $('.theme-panel [name=header-fixed] option[value="1"]').prop("selected", !0), $("#header").addClass("navbar-fixed-top"), $("#page-container").addClass("page-header-fixed")), $("#page-container").addClass("page-sidebar-fixed"), $("#page-container").hasClass("page-sidebar-minified") || generateSlimScroll($('.sidebar [data-scrollbar="true"]'))) : ($("#page-container").removeClass("page-sidebar-fixed"), 0 !== $(".sidebar .slimScrollDiv").length && ($(window).width() <= 979 ? $(".sidebar").each(function() {
            if (!$("#page-container").hasClass("page-with-two-sidebar") || !$(this).hasClass("sidebar-right")) {
                $(this).find(".slimScrollBar").remove(),
                $(this).find(".slimScrollRail").remove(),
                $(this).find('[data-scrollbar="true"]').removeAttr("style");
                var e = $(this).find('[data-scrollbar="true"]').parent(),
                a = $(e).html();
                $(e).replaceWith(a)
            }
        }) : $(window).width() > 979 && ($('.sidebar [data-scrollbar="true"]').slimScroll({
            destroy: !0
        }), $('.sidebar [data-scrollbar="true"]').removeAttr("style"))), 0 === $("#page-container .sidebar-bg").length && $("#page-container").append('<div class="sidebar-bg"></div>'))
    }),
    $(".theme-panel [name=header-fixed]").live("change",
    function() {
        1 == $(this).val() ? ($("#header").addClass("navbar-fixed-top"), $("#page-container").addClass("page-header-fixed"), $.cookie("header-fixed", !0)) : (1 == $(".theme-panel [name=sidebar-fixed]").val() && (alert("Default Header with Fixed Sidebar option is not supported. Proceed with Default Header with Default Sidebar."), $('.theme-panel [name=sidebar-fixed] option[value="2"]').prop("selected", !0), $("#page-container").removeClass("page-sidebar-fixed"), 0 === $("#page-container .sidebar-bg").length && $("#page-container").append('<div class="sidebar-bg"></div>')), $("#header").removeClass("navbar-fixed-top"), $("#page-container").removeClass("page-header-fixed"), $.cookie("header-fixed", !1))
    })
},
handleThemePanelExpand = function() {
    $('[data-click="theme-panel-expand"]').live("click",
    function() {
        var e = ".theme-panel",
        a = "active";
        $(e).hasClass(a) ? $(e).removeClass(a) : $(e).addClass(a)
    })
},
handleAfterPageLoadAddClass = function() {
    0 !== $("[data-pageload-addclass]").length && $(window).load(function() {
        $("[data-pageload-addclass]").each(function() {
            var e = $(this).attr("data-pageload-addclass");
            $(this).addClass(e)
        })
    })
},
handleSavePanelPosition = function(e) {
    "use strict";
    if (0 !== $(".ui-sortable").length) {
        var a = [],
        t = 0;
        $.when($(".ui-sortable").each(function() {
            var e = $(this).find("[data-sortable-id]");
            if (0 !== e.length) {
                var i = [];
                $(e).each(function() {
                    var e = $(this).attr("data-sortable-id");
                    i.push({
                        id: e
                    })
                }),
                a.push(i)
            } else a.push([]);
            t++
        })).done(function() {
            var t = window.location.href;
            t = t.split("?"),
            t = t[0],
            localStorage.setItem(t, JSON.stringify(a)),
            $(e).find('[data-id="title-spinner"]').delay(500).fadeOut(500,
            function() {
                $(this).remove()
            })
        })
    }
},
handleLocalStorage = function() {
    "use strict";
    if ("undefined" != typeof Storage) {
        var e = window.location.href;
        e = e.split("?"),
        e = e[0];
        var a = localStorage.getItem(e);
        if (a) {
            a = JSON.parse(a);
            var t = 0;
            $(".panel").parent('[class*="col-"]').each(function() {
                var e = a[t],
                i = $(this);
                e && $.each(e,
                function(e, a) {
                    var t = '[data-sortable-id="' + a.id + '"]';
                    if (0 !== $(t).length) {
                        var l = $(t).clone();
                        $(t).remove(),
                        $(i).append(l)
                    }
                }),
                t++
            })
        }
    } else alert("Your browser is not supported with the local storage")
},
handleResetLocalStorage = function() {
    "use strict";
    $("[data-click=reset-local-storage]").live("click",
    function(e) {
        e.preventDefault();
        var a = '<div class="modal fade" data-modal-id="reset-local-storage-confirmation">    <div class="modal-dialog">        <div class="modal-content">            <div class="modal-header">                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>                <h4 class="modal-title"><i class="fa fa-refresh m-r-5"></i> Reset Local Storage Confirmation</h4>            </div>            <div class="modal-body">                <div class="alert alert-info m-b-0">Would you like to RESET all your saved widgets and clear Local Storage?</div>            </div>            <div class="modal-footer">                <a href="javascript:;" class="btn btn-sm btn-white" data-dismiss="modal"><i class="fa fa-close"></i> No</a>                <a href="javascript:;" class="btn btn-sm btn-inverse" data-click="confirm-reset-local-storage"><i class="fa fa-check"></i> Yes</a>            </div>        </div>    </div></div>';
        $("body").append(a),
        $('[data-modal-id="reset-local-storage-confirmation"]').modal("show")
    }),
    $('[data-modal-id="reset-local-storage-confirmation"]').live("hidden.bs.modal",
    function() {
        $('[data-modal-id="reset-local-storage-confirmation"]').remove()
    }),
    $("[data-click=confirm-reset-local-storage]").live("click",
    function(e) {
        e.preventDefault();
        var a = window.location.href;
        a = a.split("?"),
        a = a[0],
        localStorage.removeItem(a),
        window.location.href = document.URL
    })
},
handleIEFullHeightContent = function() {
    var e = window.navigator.userAgent,
    a = e.indexOf("MSIE "); (a > 0 || navigator.userAgent.match(/Trident.*rv\:11\./)) && $('.vertical-box-row [data-scrollbar="true"][data-height="100%"]').each(function() {
        var e = $(this).closest(".vertical-box-row"),
        a = $(e).height();
        $(e).find(".vertical-box-cell").height(a)
    })
},
handleUnlimitedTabsRender = function() {
    function e(e, a) {
        var t = (parseInt($(e).css("margin-left")), $(e).width()),
        i = $(e).find("li.active").width(),
        l = a > -1 ? a: 150,
        n = 0;
        if ($(e).find("li.active").prevAll().each(function() {
            i += $(this).width()
        }), $(e).find("li").each(function() {
            n += $(this).width()
        }), i >= t) {
            var s = i - t;
            n != i && (s += 40),
            $(e).find(".nav.nav-tabs").animate({
                marginLeft: "-" + s + "px"
            },
            l)
        }
        i != n && n >= t ? $(e).addClass("overflow-right") : $(e).removeClass("overflow-right"),
        i >= t && n >= t ? $(e).addClass("overflow-left") : $(e).removeClass("overflow-left")
    }
    function a(e, a) {
        var t = $(e).closest(".tab-overflow"),
        i = parseInt($(t).find(".nav.nav-tabs").css("margin-left")),
        l = $(t).width(),
        n = 0,
        s = 0;
        switch ($(t).find("li").each(function() {
            $(this).hasClass("next-button") || $(this).hasClass("prev-button") || (n += $(this).width())
        }), a) {
        case "next":
            var o = n + i - l;
            l >= o ? (s = o - i, setTimeout(function() {
                $(t).removeClass("overflow-right")
            },
            150)) : s = l - i - 80,
            0 != s && $(t).find(".nav.nav-tabs").animate({
                marginLeft: "-" + s + "px"
            },
            150,
            function() {
                $(t).addClass("overflow-left")
            });
            break;
        case "prev":
            var o = -i;
            l >= o ? ($(t).removeClass("overflow-left"), s = 0) : s = o - l + 80,
            $(t).find(".nav.nav-tabs").animate({
                marginLeft: "-" + s + "px"
            },
            150,
            function() {
                $(t).addClass("overflow-right")
            })
        }
    }
    function t() {
        $(".tab-overflow").each(function() {
            var a = $(this).width(),
            t = 0,
            i = $(this),
            l = a;
            $(i).find("li").each(function() {
                var e = $(this);
                t += $(e).width(),
                $(e).hasClass("active") && t > a && (l -= t)
            }),
            e(this, 0)
        })
    }
    $('[data-click="next-tab"]').live("click",
    function(e) {
        e.preventDefault(),
        a(this, "next")
    }),
    $('[data-click="prev-tab"]').live("click",
    function(e) {
        e.preventDefault(),
        a(this, "prev")
    }),
    $(window).resize(function() {
        $(".tab-overflow .nav.nav-tabs").removeAttr("style"),
        t()
    }),
    t()
},
handleMobileSidebar = function() {
    "use strict";
    /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) && $("#page-container").hasClass("page-sidebar-minified") && ($('#sidebar [data-scrollbar="true"]').css("overflow", "visible"), $('.page-sidebar-minified #sidebar [data-scrollbar="true"]').slimScroll({
        destroy: !0
    }), $('.page-sidebar-minified #sidebar [data-scrollbar="true"]').removeAttr("style"), $(".page-sidebar-minified #sidebar [data-scrollbar=true]").trigger("mouseover"));
    var e = 0;
    $(".page-sidebar-minified .sidebar [data-scrollbar=true] a").live("touchstart",
    function(a) {
        var t = a.originalEvent.touches[0] || a.originalEvent.changedTouches[0],
        i = t.pageY;
        e = i - parseInt($(this).closest("[data-scrollbar=true]").css("margin-top"))
    }),
    $(".page-sidebar-minified .sidebar [data-scrollbar=true] a").live("touchmove",
    function(a) {
        if (a.preventDefault(), /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
            var t = a.originalEvent.touches[0] || a.originalEvent.changedTouches[0],
            i = t.pageY,
            l = i - e;
            $(this).closest("[data-scrollbar=true]").css("margin-top", l + "px")
        }
    }),
    $(".page-sidebar-minified .sidebar [data-scrollbar=true] a").live("touchend",
    function() {
        var a = $(this).closest("[data-scrollbar=true]"),
        t = $(window).height(),
        i = parseInt($("#sidebar").css("padding-top")),
        l = $("#sidebar").height();
        e = $(a).css("margin-top");
        var n = i;
        $(".sidebar").not(".sidebar-right").find(".nav").each(function() {
            n += $(this).height()
        });
        var s = -parseInt(e) + $(".sidebar").height();
        if (s >= n && n >= t && n >= l) {
            var o = t - n - 20;
            $(a).animate({
                marginTop: o + "px"
            })
        } else parseInt(e) >= 0 || l >= n ? $(a).animate({
            marginTop: "0px"
        }) : (o = e, $(a).animate({
            marginTop: o + "px"
        }))
    })
},
App = function() {
    "use strict";
    return {
        init: function() {
        	/*
            handleDraggablePanel(),
            handleLocalStorage(),
            handleResetLocalStorage(),
            handleSlimScroll(),*/
            handleSidebarMenu(),
            /*
            handleMobileSidebarToggle(),
            */
            handleSidebarMinify(),
            /*
            handleMobileSidebar(),
            handleThemePageStructureControl(),
            handleThemePanelExpand(),
            handleAfterPageLoadAddClass(),
            handlePanelAction(),
            handelTooltipPopoverActivation(),
            handleScrollToTopButton(),
            handlePageContentView(),
            handleIEFullHeightContent(),
            handleUnlimitedTabsRender()*/
        	handleAfterPageLoadAddClass(),
        	handlePageContentView()
        }
    }
} ();