<#-- @ftlvariable name="feed" type="com.somanyfeeds.feedadmin.FeedView" -->

<#import "layout/layout.ftl" as layout>
<@layout.adminLayout>
<h1>Edit Feed</h1>

<dl>
    <form action="/feeds/${feed.id}" method="post" class="card">
        <dt>Id</dt>
        <dd>${feed.id}</dd>
        <label>
            <dt>Name</dt>
            <dd><input type="text" value="${feed.name}"/></dd>
        </label>
        <label>
            <dt>Slug</dt>
            <dd><input type="text" value="${feed.slug}"/></dd>
        </label>
        <label>
            <dt>Info</dt>
            <dd><input type="text" value="${feed.info}"/></dd>
        </label>
        <label>
            <dt>Type</dt>
            <dd><input type="text" value="${feed.type}"/></dd>
        </label>

        <nav>
            <input type="submit" value="Save" class="button"/>
            <a href="/feeds">Cancel</a>
        </nav>
    </form>
</dl>
</@layout.adminLayout>
