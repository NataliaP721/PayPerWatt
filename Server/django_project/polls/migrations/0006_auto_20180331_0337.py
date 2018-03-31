# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('polls', '0005_auto_20180326_2039'),
    ]

    operations = [
        migrations.AlterField(
            model_name='payperwatt',
            name='chargedAmount',
            field=models.FloatField(default=0),
        ),
    ]
