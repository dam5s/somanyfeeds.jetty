<#-- @ftlvariable name="feeds" type="java.util.List<com.somanyfeeds.feedadmin.FeedView>" -->

<h1>Feeds!!</h1>

<ul>
<#list feeds as feed>
    <li>
        <dl>
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

        <a href="feeds/${feed.id}/edit">Edit</a>
    </li>
</#list>
</ul>
