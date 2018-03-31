# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('polls', '0006_auto_20180331_0337'),
    ]

    operations = [
        migrations.RenameField(
            model_name='payperwatt',
            old_name='chargedAmount',
            new_name='chargeAmount',
        ),
    ]
