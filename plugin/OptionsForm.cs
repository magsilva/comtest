using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using EnvDTE80;

namespace JYU.ComTestCSPlugin
{
    public partial class OptionsForm : Form
    {
        public ComTestSettings Settings { get; private set; }

        private DTE2 dte;

        public OptionsForm( DTE2 dte )
        {
            InitializeComponent();
            this.dte = dte;

            // Load data from global config
            Settings = ComTestSettings.Load( dte );
            JavaPath.Text = Settings.JavaPath;
            ComTestPath.Text = Settings.ComtestPath;
        }

        private void ButtonOK_Click( object sender, EventArgs e )
        {
            Settings.JavaPath = JavaPath.Text;
            Settings.ComtestPath = ComTestPath.Text;

            // Save data to global config
            Settings.Save( dte );

            Close();
        }

        private void ButtonCancel_Click( object sender, EventArgs e )
        {
            Close();
        }

        private void BrowseJavaButton_Click( object sender, EventArgs e )
        {
            if ( openExeDialog.ShowDialog( this ) == System.Windows.Forms.DialogResult.OK )
            {
                JavaPath.Text = openExeDialog.FileName;
            }
        }

        private void BrowseCTButton_Click( object sender, EventArgs e )
        {
            if ( openJarDialog.ShowDialog( this ) == System.Windows.Forms.DialogResult.OK )
            {
                ComTestPath.Text = openJarDialog.FileName;
            }
        }
    }
}
