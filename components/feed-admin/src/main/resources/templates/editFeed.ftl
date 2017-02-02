<#-- @ftlvariable name="feed" type="com.somanyfeeds.feedadmin.FeedView" -->

<#import "layout/layout.ftl" as layout>
<@layout.adminLayout>
<h1>Edit Feed</h1>

<dl class="card">
    <dt>id</dt>
    <dd>${feed.id}</dd>
    <dt>name</dt>
    <dd>${feed.name}</dd>
    <dt>slug</dt>
    <dd>${feed.slug}</dd>
    <dt>info</dt>
    <dd>${feed.info}</dd>
    <dt>type</dt>
    <dd>${feed.type}</dd>
</dl>
</@layout.adminLayout>
