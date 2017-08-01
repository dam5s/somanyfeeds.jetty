<#-- @ftlvariable name="feed" type="com.somanyfeeds.feedadmin.FeedView" -->
<#-- @ftlvariable name="notification" type="java.lang.String" -->

<#import "layout/layout.ftl" as layout>
<@layout.adminLayout>
<h1>View Feed</h1>

    <#if notification??>
    <div class="card notification">${notification}</div>
    </#if>

<dl class="card">
    <dt>Id</dt>
    <dd>${feed.id}</dd>
    <dt>Name</dt>
    <dd>${feed.name}</dd>
    <dt>Slug</dt>
    <dd>${feed.slug}</dd>
    <dt>Info</dt>
    <dd>${feed.info}</dd>
    <dt>Type</dt>
    <dd>${feed.type}</dd>

    <nav>
        <a href="/feeds/${feed.id}/edit">Edit</a>
        <a href="/feeds">Back to list</a>
    </nav>
</dl>

</@layout.adminLayout>
