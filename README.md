# Jenkins Build Notifications Plugin

This is a plugin to enable build notifications through [Pushover][], [Telegram][], [Slack][] or even [Boteco][].

## How to build

Just execute a `mvn package` and upload the *hpi* package to your Jenkins instance.

## How to configure

There are global and specific options:

### Global options

Global options should be configured in Jenkins System Configuration. You'll need to set
your api tokens there.

### Specific options

There are per-job configurations. You need to add a post-build step (there is a separated
step for each notification service) and configure the target to receive notifications.

## How you'll be notified

Notifications will include:

- The project's name
- The build number
- The build result
- The build's changes
- The build link

If you are receiving notifications through Pushover, the notification will be sent with
high priority in case of a fail build, low priority in case of a success build and normal
priority for the other cases.

Note that Telegram doesn't have a way to set priority for messages (and is understandable
because Telegram is a chat platform and not a notification platform like Pushover).

## How to contribute

Open an issue, spread the project, use it, fork it...

[pushover]: <http://pushover.net/>
[telegram]: <https://telegram.org/>
[boteco]: <https://github.com/devnull-tools/boteco>
[slack]: <https://slack.com>
