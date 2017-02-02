<#-- @ftlvariable name="feeds" type="java.util.List<com.somanyfeeds.feedadmin.FeedView>" -->

<#import "layout/layout.ftl" as layout>
<@layout.adminLayout>
<h1>List of Feeds</h1>

<#list feeds as feed>
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

    <dt>actions</dt>
    <dd>
        <a href="feeds/${feed.id}/edit">Edit</a>
    </dd>
</dl>
</#list>
</@layout.adminLayout>
