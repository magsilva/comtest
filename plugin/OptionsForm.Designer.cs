namespace JYU.ComTestCSPlugin
{
    partial class OptionsForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose( bool disposing )
        {
            if ( disposing && ( components != null ) )
            {
                components.Dispose();
            }
            base.Dispose( disposing );
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.label1 = new System.Windows.Forms.Label();
            this.ComTestPath = new System.Windows.Forms.TextBox();
            this.BrowseCTButton = new System.Windows.Forms.Button();
            this.ButtonOK = new System.Windows.Forms.Button();
            this.ButtonCancel = new System.Windows.Forms.Button();
            this.BrowseJavaButton = new System.Windows.Forms.Button();
            this.JavaPath = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.openExeDialog = new System.Windows.Forms.OpenFileDialog();
            this.openJarDialog = new System.Windows.Forms.OpenFileDialog();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point( 12, 120 );
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size( 155, 13 );
            this.label1.TabIndex = 3;
            this.label1.Text = "Path to ComTest.jar executable";
            // 
            // ComTestPath
            // 
            this.ComTestPath.Location = new System.Drawing.Point( 21, 142 );
            this.ComTestPath.Name = "ComTestPath";
            this.ComTestPath.Size = new System.Drawing.Size( 298, 20 );
            this.ComTestPath.TabIndex = 4;
            // 
            // BrowseCTButton
            // 
            this.BrowseCTButton.Location = new System.Drawing.Point( 316, 142 );
            this.BrowseCTButton.Name = "BrowseCTButton";
            this.BrowseCTButton.Size = new System.Drawing.Size( 22, 20 );
            this.BrowseCTButton.TabIndex = 5;
            this.BrowseCTButton.Text = "...";
            this.BrowseCTButton.UseVisualStyleBackColor = true;
            this.BrowseCTButton.Click += new System.EventHandler( this.BrowseCTButton_Click );
            // 
            // ButtonOK
            // 
            this.ButtonOK.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.ButtonOK.Location = new System.Drawing.Point( 82, 191 );
            this.ButtonOK.Name = "ButtonOK";
            this.ButtonOK.Size = new System.Drawing.Size( 75, 23 );
            this.ButtonOK.TabIndex = 6;
            this.ButtonOK.Text = "&OK";
            this.ButtonOK.UseVisualStyleBackColor = true;
            this.ButtonOK.Click += new System.EventHandler( this.ButtonOK_Click );
            // 
            // ButtonCancel
            // 
            this.ButtonCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.ButtonCancel.Location = new System.Drawing.Point( 209, 191 );
            this.ButtonCancel.Name = "ButtonCancel";
            this.ButtonCancel.Size = new System.Drawing.Size( 75, 23 );
            this.ButtonCancel.TabIndex = 7;
            this.ButtonCancel.Text = "&Cancel";
            this.ButtonCancel.UseVisualStyleBackColor = true;
            this.ButtonCancel.Click += new System.EventHandler( this.ButtonCancel_Click );
            // 
            // BrowseJavaButton
            // 
            this.BrowseJavaButton.Location = new System.Drawing.Point( 316, 77 );
            this.BrowseJavaButton.Name = "BrowseJavaButton";
            this.BrowseJavaButton.Size = new System.Drawing.Size( 22, 20 );
            this.BrowseJavaButton.TabIndex = 2;
            this.BrowseJavaButton.Text = "...";
            this.BrowseJavaButton.UseVisualStyleBackColor = true;
            this.BrowseJavaButton.Click += new System.EventHandler( this.BrowseJavaButton_Click );
            // 
            // JavaPath
            // 
            this.JavaPath.Location = new System.Drawing.Point( 21, 77 );
            this.JavaPath.Name = "JavaPath";
            this.JavaPath.Size = new System.Drawing.Size( 298, 20 );
            this.JavaPath.TabIndex = 1;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point( 12, 55 );
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size( 166, 13 );
            this.label2.TabIndex = 0;
            this.label2.Text = "Path to Java interpreter (java.exe)";
            // 
            // openExeDialog
            // 
            this.openExeDialog.FileName = "java.exe";
            this.openExeDialog.Filter = "Executable files|*.exe|All files|*.*";
            this.openExeDialog.Title = "Java.exe location";
            // 
            // openJarDialog
            // 
            this.openJarDialog.FileName = "ComTest.jar";
            this.openJarDialog.Filter = "ComTest.jar|ComTest.jar|JAR files|*.jar|All files|*.*";
            this.openJarDialog.Title = "ComTest.jar location";
            // 
            // OptionsForm
            // 
            this.AcceptButton = this.ButtonOK;
            this.AutoScaleDimensions = new System.Drawing.SizeF( 6F, 13F );
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.ButtonCancel;
            this.ClientSize = new System.Drawing.Size( 361, 287 );
            this.Controls.Add( this.BrowseJavaButton );
            this.Controls.Add( this.JavaPath );
            this.Controls.Add( this.label2 );
            this.Controls.Add( this.ButtonCancel );
            this.Controls.Add( this.ButtonOK );
            this.Controls.Add( this.BrowseCTButton );
            this.Controls.Add( this.ComTestPath );
            this.Controls.Add( this.label1 );
            this.Name = "OptionsForm";
            this.Text = "ComTest Plugin Options";
            this.ResumeLayout( false );
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox ComTestPath;
        private System.Windows.Forms.Button BrowseCTButton;
        private System.Windows.Forms.Button ButtonOK;
        private System.Windows.Forms.Button ButtonCancel;
        private System.Windows.Forms.Button BrowseJavaButton;
        private System.Windows.Forms.TextBox JavaPath;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.OpenFileDialog openExeDialog;
        private System.Windows.Forms.OpenFileDialog openJarDialog;
    }
}