(window.webpackJsonp=window.webpackJsonp||[]).push([[5],{280:function(t,e,n){var content=n(282);content.__esModule&&(content=content.default),"string"==typeof content&&(content=[[t.i,content,""]]),content.locals&&(t.exports=content.locals);(0,n(109).default)("223a3fea",content,!0,{sourceMap:!1})},281:function(t,e,n){"use strict";n(280)},282:function(t,e,n){var c=n(108)(!1);c.push([t.i,'.wrapper[data-v-aca85150]{padding:1.5em}.title[data-v-aca85150]{font-family:"leda-bold",monospace;color:#fff}.field[data-v-aca85150]{margin:0;padding:.625em 0}.divider[data-v-aca85150]{width:100vw;height:1px;background-color:#575757;margin-left:-1.5em}.advanced[data-v-aca85150]{padding:18px 0}',""]),t.exports=c},283:function(t,e,n){"use strict";n.r(e);n(15);var c={mounted:function(){var t=this;fetch("/api/interaction").then((function(t){return t.text()})).then((function(data){t.$refs.interaction.checked="1"===data})),fetch("/api/hand-interaction").then((function(t){return t.text()})).then((function(data){t.$refs.handInteraction.checked="1"===data})),fetch("/api/brightness").then((function(t){return t.text()})).then((function(data){t.$refs.brightness.value=parseFloat(data)}))},methods:{enableInteraction:function(){console.log(this.$refs.interaction.checked);var t=this.$refs.interaction.checked?"1":"0";fetch("/api/interaction?value=".concat(t))},enableHandInteraction:function(){console.log(this.$refs.handInteraction.checked);var t=this.$refs.handInteraction.checked?"1":"0";fetch("/api/interaction?value=".concat(t))},updateBrightness:function(){console.log(this.$refs.brightness.value),fetch("/api/brightness?value=".concat(this.$refs.brightness.value))}}},r=(n(281),n(46)),component=Object(r.a)(c,(function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("section",{staticClass:"wrapper"},[n("h1",{staticClass:"title"},[t._v("LEDA Controller")]),t._v(" "),n("div",{staticClass:"form__wrapper"},[n("form",{staticClass:"form",attrs:{method:"get"}},[n("div",{staticClass:"field"},[n("input",{ref:"interaction",staticClass:"switch is-rounded",attrs:{id:"interaction",type:"checkbox",name:"interaction",checked:"checked"},on:{click:function(e){return t.enableInteraction()}}}),t._v(" "),n("label",{attrs:{for:"interaction"}},[t._v("Interaction")])]),t._v(" "),n("div",{staticClass:"divider"}),t._v(" "),n("div",{staticClass:"field"},[n("input",{ref:"handInteraction",staticClass:"switch is-rounded",attrs:{id:"handInteraction",type:"checkbox",name:"handInteraction"},on:{click:function(e){return t.enableHandInteraction()}}}),t._v(" "),n("label",{attrs:{for:"handInteraction"}},[t._v("Hand Interaction")])]),t._v(" "),n("div",{staticClass:"divider"}),t._v(" "),n("div",{staticClass:"field"},[n("label",{attrs:{for:"brightness"}},[t._v("Brightness")]),t._v(" "),n("input",{ref:"brightness",staticClass:"slider is-circle is-medium is-fullwidth",attrs:{id:"brightness",type:"range",name:"brightness",value:"0",min:"0",max:"1",step:"0.25"},on:{change:function(e){return t.updateBrightness()}}})])]),t._v(" "),n("div",{staticClass:"divider"})]),t._v(" "),n("div",{staticClass:"help__wrapper"})])}),[],!1,null,"aca85150",null);e.default=component.exports}}]);