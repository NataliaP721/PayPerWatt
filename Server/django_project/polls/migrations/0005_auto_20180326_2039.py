# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('polls', '0004_auto_20180326_1946'),
    ]

    operations = [
        migrations.AlterField(
            model_name='payperwatt',
            name='id',
            field=models.IntegerField(default=0, serialize=False, primary_key=True),
        ),
    ]
