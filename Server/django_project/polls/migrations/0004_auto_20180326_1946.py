# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('polls', '0003_auto_20180326_1902'),
    ]

    operations = [
        migrations.RenameModel(
            old_name='PayPerWattDB',
            new_name='PayPerWatt',
        ),
    ]
