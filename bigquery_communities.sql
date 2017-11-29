SELECT
    author,
    subreddit,
    score,
    body
FROM
    [fh-bigquery:reddit_comments.2017_09]
WHERE
    author != "[deleted]"
